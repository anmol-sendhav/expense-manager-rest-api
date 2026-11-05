import { useEffect, useState } from "react";
import {
  UserRound,
  Mail,
  Phone,
  MapPin,
  CalendarDays,
  Pencil,
  Save,
  X,
  RefreshCw,
  ShieldCheck
} from "lucide-react";


import client from "../api/client";
import { API } from "../api/endpoints";
import { useAuth } from "../context/AuthContext";
import PageHeader from "../components/PageHeader";
import { errorMessage } from "../utils/format";
import { Camera, Upload, Trash2 } from "lucide-react";


const emptyProfile = {
  name: "",
  email: "",
  age: "",
  phone: "",
  address: "",
  city: ""
};

export default function Settings() {
  const {user, updateUser } = useAuth();

  const [profile, setProfile] = useState(emptyProfile);
  const [selectedPhoto, setSelectedPhoto] = useState(null);
  const [photoPreview, setPhotoPreview] = useState("");
  const [photoUploading, setPhotoUploading] = useState(false);
  const [form, setForm] = useState(emptyProfile);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [editing, setEditing] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
 

  async function loadProfile() {
    setLoading(true);
    setError("");
	
    try {
      const response = await client.get(API.profile);
      const data = response.data;

	  const normalized = {
	    name: data.name || "",
	    email: data.email || "",
	    age: data.age ?? "",
	    phone: data.phone || "",
	    address: data.address || "",
	    city: data.city || "",
	    picture: data.picture || ""
	  };

      setProfile(normalized);
      setForm(normalized);
    } catch (err) {
      setError(errorMessage(err));
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadProfile();
  }, []);

  function handleChange(event) {
    const { name, value } = event.target;

    setForm(previous => ({
      ...previous,
      [name]: value
    }));
  }
  
  function handlePhotoSelect(event) {
    const file = event.target.files?.[0];

    if (!file) return;

    const allowedTypes = ["image/jpeg", "image/png", "image/webp"];

    if (!allowedTypes.includes(file.type)) {
      setError("Please select a JPG, PNG or WEBP image.");
      return;
    }

    if (file.size > 5 * 1024 * 1024) {
      setError("Image must be 5 MB or smaller.");
      return;
    }

    setError("");
    setSelectedPhoto(file);

    setPhotoPreview(URL.createObjectURL(file));
  }
  
  const handlePhotoUpload = async () => {
    if (!selectedPhoto) return;

    setPhotoUploading(true);

    try {
      const formData = new FormData();
      formData.append("file", selectedPhoto);

      const response = await client.post(
        `${API.profile}/photo`,
        formData
      );

      const picture = response.data.picture;

      setProfile((prev) => ({
        ...prev,
        picture
      }));

      setForm((prev) => ({
        ...prev,
        picture
      }));

      updateUser({ picture });

      setSelectedPhoto(null);
      setPhotoPreview("");

    } catch (error) {
      setError(errorMessage(error));
    } finally {
      setPhotoUploading(false);
    }
  };

  
  
  function cancelPhotoSelection() {
    setSelectedPhoto(null);
    setPhotoPreview("");
  }

  function cancelEditing() {
    setForm(profile);
    setEditing(false);
    setError("");
    setSuccess("");
  }

  async function handleSave(event) {
    event.preventDefault();

    setError("");
    setSuccess("");

    if (!form.name.trim() || !form.phone.trim() || !form.city.trim()) {
      setError("Name, phone number and city are required.");
      return;
    }

    if (form.age !== "" &&
        (!Number.isInteger(Number(form.age)) ||
         Number(form.age) < 1 ||
         Number(form.age) > 120)) {
      setError("Please enter a valid age between 1 and 120.");
      return;
    }

    const payload = {
      name: form.name.trim(),
      age: form.age === "" ? null : Number(form.age),
      phone: form.phone.trim(),
      address: form.address.trim(),
      city: form.city.trim()
    };

    setSaving(true);

    try {
      const response = await client.put(API.profile, payload);

      const updated = {
        name: response.data.name || "",
        email: response.data.email || profile.email,
        age: response.data.age ?? "",
        phone: response.data.phone || "",
        address: response.data.address || "",
        city: response.data.city || ""
      };

      setProfile(updated);
      setForm(updated);
      updateUser(updated);
      setEditing(false);
      setSuccess("Your profile has been updated successfully.");
    } catch (err) {
      setError(errorMessage(err));
    } finally {
      setSaving(false);
    }
  }

  const initials = profile.name
    ? profile.name.trim().charAt(0).toUpperCase()
    : "U";

  return (
    <>
      <PageHeader
        title="Account Settings"
        subtitle="Manage your personal information and account details."
        action={
          !loading && !editing ? (
            <button
              className="button button-primary"
              onClick={() => {
                setForm(profile);
                setEditing(true);
                setError("");
                setSuccess("");
              }}
            >
              <Pencil size={16} />
              Edit profile
            </button>
          ) : null
        }
      />

      {error && <div className="alert error">{error}</div>}
      {success && <div className="alert success">{success}</div>}

      {loading ? (
        <div className="panel empty-state">Loading your profile...</div>
      ) : (
        <div className="settings-layout">
          <section className="panel settings-profile-card">
            <div className="settings-profile-cover" />

			<div className="settings-avatar-wrapper">
			  <div className="settings-avatar">
			    {photoPreview || profile.picture ? (
			      <img
			        src={
			          photoPreview ||
			          (profile.picture.startsWith("http")
			            ? profile.picture
			            : `http://localhost:8080${profile.picture}`)
			        }
			        alt="Profile"
			      />
			    ) : (
			      initials
			    )}
			  </div>

			  <label className="settings-photo-button" title="Choose profile photo">
			    <Camera size={16} />
			    <input
			      type="file"
			      accept="image/png,image/jpeg,image/webp"
			      onChange={handlePhotoSelect}
			      hidden
			    />
			  </label>
			</div>

			{selectedPhoto && (
			  <div className="settings-photo-actions">
			    <button
			      className="button button-primary"
			      onClick={handlePhotoUpload}
			      disabled={photoUploading}
			    >
			      <Upload size={15} />
			      {photoUploading ? "Uploading..." : "Upload photo"}
			    </button>

			    <button
			      className="button button-outline"
			      onClick={cancelPhotoSelection}
			      disabled={photoUploading}
			    >
			      <Trash2 size={15} />
			      Cancel
			    </button>
			  </div>
			)}

            <h2>{profile.name || "Your account"}</h2>
            <p>{profile.email}</p>

            <span className="settings-account-badge">
              <ShieldCheck size={14} />
              Verified account session
            </span>

            <div className="settings-profile-note">
              Your financial information is linked to this account.
            </div>
          </section>

          <section className="panel settings-details-card">
            <div className="settings-section-heading">
              <div>
                <h2>Personal information</h2>
                <p>Keep your account details up to date.</p>
              </div>
              <UserRound size={20} />
            </div>

            <form onSubmit={handleSave}>
              <div className="settings-fields-grid">
                <label className="settings-field">
                  Full name
                  <span>
                    <UserRound size={16} />
                    <input
                      name="name"
                      value={form.name}
                      onChange={handleChange}
                      disabled={!editing}
                      placeholder="Enter your full name"
                      maxLength={100}
                      required
                    />
                  </span>
                </label>

                <label className="settings-field">
                  Email address
                  <span>
                    <Mail size={16} />
                    <input
                      value={profile.email}
                      disabled
                      readOnly
                    />
                  </span>
                  <small>Email is currently your login identity.</small>
                </label>

                <label className="settings-field">
                  Age
                  <span>
                    <CalendarDays size={16} />
                    <input
                      name="age"
                      type="number"
                      min="1"
                      max="120"
                      value={form.age}
                      onChange={handleChange}
                      disabled={!editing}
                      placeholder="Enter your age"
                    />
                  </span>
                </label>

                <label className="settings-field">
                  Phone number
                  <span>
                    <Phone size={16} />
                    <input
                      name="phone"
                      value={form.phone}
                      onChange={handleChange}
                      disabled={!editing}
                      placeholder="Enter phone number"
                      maxLength={20}
                      required
                    />
                  </span>
                </label>

                <label className="settings-field">
                  City
                  <span>
                    <MapPin size={16} />
                    <input
                      name="city"
                      value={form.city}
                      onChange={handleChange}
                      disabled={!editing}
                      placeholder="Enter your city"
                      maxLength={100}
                      required
                    />
                  </span>
                </label>

                <label className="settings-field">
                  Address
                  <span>
                    <MapPin size={16} />
                    <input
                      name="address"
                      value={form.address}
                      onChange={handleChange}
                      disabled={!editing}
                      placeholder="Enter your address"
                      maxLength={255}
                    />
                  </span>
                </label>
              </div>

              {editing && (
                <div className="settings-form-actions">
                  <button
                    type="button"
                    className="button button-outline"
                    onClick={cancelEditing}
                    disabled={saving}
                  >
                    <X size={16} />
                    Cancel
                  </button>

                  <button
                    type="submit"
                    className="button button-primary"
                    disabled={saving}
                  >
                    <Save size={16} />
                    {saving ? "Saving..." : "Save changes"}
                  </button>
                </div>
              )}
            </form>
          </section>
        </div>
      )}

      <div className="settings-refresh">
        <button
          className="button button-outline"
          onClick={loadProfile}
          disabled={loading || saving}
        >
          <RefreshCw size={15} />
          Refresh profile
        </button>
      </div>
    </>
  );
}