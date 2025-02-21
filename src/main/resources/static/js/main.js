document.addEventListener("DOMContentLoaded", () => {
    checkUserAuth();
    setupLogout();
});

function checkUserAuth() {
    const token = localStorage.getItem("jwtToken");
    const loginLink = document.getElementById("loginLink");
    const registerLink = document.getElementById("registerLink");
    const profileLink = document.getElementById("profileLink");
    const logoutButton = document.getElementById("logoutButton");

    if (token) {
        console.log("🔒 User is authenticated");
        if (loginLink) loginLink.style.display = "none";
        if (registerLink) registerLink.style.display = "none";
        if (profileLink) profileLink.style.display = "inline-block";
        if (logoutButton) logoutButton.style.display = "inline-block";
    } else {
        console.log("🔓 User is not authenticated");
        if (profileLink) profileLink.style.display = "none";
        if (logoutButton) logoutButton.style.display = "none";
    }
}

function setupLogout() {
    const logoutButton = document.getElementById("logoutButton");
    if (logoutButton) {
        logoutButton.addEventListener("click", () => {
            localStorage.removeItem("jwtToken");
            window.location.href = "login.html";
        });
    }
}