
const API_URL = "/api/incidents";

// HTML elements
const incidentForm = document.getElementById("incidentForm");
const imageInput = document.getElementById("image");
const imagePreview = document.getElementById("imagePreview");
const imagePreviewContainer = document.getElementById("imagePreviewContainer");
const message = document.getElementById("message");

const locationButton = document.getElementById("locationButton");
const refreshButton = document.getElementById("refreshButton");

const MAX_IMAGE_SIZE = 5 * 1024 * 1024;

let previewURL = null;


// Display messages
function showMessage(text, success = true) {
    message.textContent = text;
    message.style.color = success ? "green" : "red";
}


// Escape HTML to safely display incident data
function escapeHTML(value) {
    return String(value ?? "").replace(/[&<>"']/g, character => ({
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        '"': "&quot;",
        "'": "&#039;"
    })[character]);
}


// IMAGE PREVIEW
imageInput.addEventListener("change", function () {

    const file = imageInput.files[0];

    if (previewURL) {
        URL.revokeObjectURL(previewURL);
        previewURL = null;
    }

    imagePreviewContainer.style.display = "none";
    imagePreview.removeAttribute("src");

    if (!file) {
        return;
    }

    const allowedTypes = [
        "image/jpeg",
        "image/png",
        "image/webp"
    ];

    if (!allowedTypes.includes(file.type)) {
        alert("Only JPG, PNG and WEBP images are allowed.");
        imageInput.value = "";
        return;
    }

    if (file.size > MAX_IMAGE_SIZE) {
        alert("Image size must not exceed 5 MB.");
        imageInput.value = "";
        return;
    }

    previewURL = URL.createObjectURL(file);

    imagePreview.src = previewURL;
    imagePreviewContainer.style.display = "block";
});


// SUBMIT INCIDENT WITH IMAGE
incidentForm.addEventListener("submit", async function (event) {

    event.preventDefault();

    const image = imageInput.files[0];

    if (!image) {
        showMessage("Please upload a waste photograph.", false);
        return;
    }

    if (image.size > MAX_IMAGE_SIZE) {
        showMessage("Image size must not exceed 5 MB.", false);
        return;
    }

    const latitude = document.getElementById("latitude").value;
    const longitude = document.getElementById("longitude").value;

    // Validate coordinates
    if ((latitude && !longitude) || (!latitude && longitude)) {
        showMessage("Please provide both latitude and longitude.", false);
        return;
    }

    if (latitude && longitude) {
        const lat = Number(latitude);
        const lng = Number(longitude);

        if (!Number.isFinite(lat) || lat < -90 || lat > 90 ||
            !Number.isFinite(lng) || lng < -180 || lng > 180) {
            showMessage("Please enter valid coordinates.", false);
            return;
        }
    }

    // Create multipart form data
    const formData = new FormData();

    formData.append(
        "title",
        document.getElementById("title").value.trim()
    );

    formData.append(
        "description",
        document.getElementById("description").value.trim()
    );

    formData.append(
        "wasteCategory",
        document.getElementById("wasteCategory").value
    );

    formData.append(
        "location",
        document.getElementById("location").value.trim()
    );

    if (latitude !== "") {
        formData.append("latitude", latitude);
    }

    if (longitude !== "") {
        formData.append("longitude", longitude);
    }

    formData.append("image", image);

    const submitButton = document.getElementById("submitButton");

    submitButton.disabled = true;
    submitButton.textContent = "Submitting...";

    try {

        const response = await fetch(
            API_URL + "/with-image",
            {
                method: "POST",
                body: formData
            }
        );

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || "Unable to submit incident.");
        }

        const savedIncident = await response.json();

        showMessage(
            "Incident submitted successfully! Incident ID: " +
            savedIncident.incidentCode
        );

        incidentForm.reset();

        if (previewURL) {
            URL.revokeObjectURL(previewURL);
            previewURL = null;
        }

        imagePreview.removeAttribute("src");
        imagePreviewContainer.style.display = "none";

        await loadIncidents();

    } catch (error) {

        console.error("Submission error:", error);

        showMessage(
            "Submission failed: " + error.message,
            false
        );

    } finally {

        submitButton.disabled = false;
        submitButton.textContent = "Submit Incident Report";
    }
});


// DETECT USER LOCATION
locationButton.addEventListener("click", function () {

    if (!navigator.geolocation) {
        alert("Geolocation is not supported by your browser.");
        return;
    }

    locationButton.textContent = "Detecting location...";

    navigator.geolocation.getCurrentPosition(

        function (position) {

            document.getElementById("latitude").value =
                position.coords.latitude;

            document.getElementById("longitude").value =
                position.coords.longitude;

            locationButton.textContent = "📍 Detect My Location";

            showMessage("Location detected successfully!");
        },

        function (error) {

            console.error(error);

            alert("Unable to detect location. Please allow location access.");

            locationButton.textContent = "📍 Detect My Location";
        }
    );
});


// LOAD INCIDENTS
async function loadIncidents() {

    const incidentsList = document.getElementById("incidentsList");

    try {

        const response = await fetch(API_URL);

        if (!response.ok) {
            throw new Error("Could not load incidents.");
        }

        const incidents = await response.json();

        // Update statistics
        document.getElementById("totalIncidents").textContent =
            incidents.length;

        const pending = incidents.filter(
            incident => !["VERIFIED", "REJECTED"].includes(incident.status)
        ).length;

        const verified = incidents.filter(
            incident => incident.status === "VERIFIED"
        ).length;

        document.getElementById("pendingIncidents").textContent = pending;

        document.getElementById("verifiedIncidents").textContent = verified;


        // Display incident records
        if (incidents.length === 0) {

            incidentsList.innerHTML =
                "<p>No incidents reported yet.</p>";

            return;
        }

        incidentsList.innerHTML = incidents.map(incident => {

            return `
                <div class="incident-card">

                    <h3>${escapeHTML(incident.title)}</h3>

                    <p>
                        <strong>Incident Code:</strong>
                        ${escapeHTML(incident.incidentCode)}
                    </p>

                    <p>
                        <strong>Category:</strong>
                        ${escapeHTML(incident.wasteCategory)}
                    </p>

                    <p>
                        <strong>Location:</strong>
                        ${escapeHTML(incident.location)}
                    </p>

                    <p>
                        <strong>Status:</strong>
                        ${escapeHTML(incident.status)}
                    </p>

                    <p>
                        <strong>Reported:</strong>
                        ${escapeHTML(incident.reportedAt)}
                    </p>

                </div>
            `;

        }).join("");

    } catch (error) {

        console.error("Loading error:", error);

        incidentsList.innerHTML =
            "<p>Unable to load incidents. Please try again.</p>";
    }
}


// REFRESH BUTTON
refreshButton.addEventListener("click", loadIncidents);


// LOAD DATA WHEN PAGE OPENS
document.addEventListener("DOMContentLoaded", loadIncidents);
loadIncidents();
