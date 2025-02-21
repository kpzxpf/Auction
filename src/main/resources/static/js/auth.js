const API_BASE_URL = "http://localhost:8080";

document.addEventListener("DOMContentLoaded", function () {
    setupLogin();
    setupRegister();
    checkAuthStatus();

    document.getElementById("logoutButton")?.addEventListener("click", logout);
});

/**
 * 📌 Проверяет аутентификацию пользователя и управляет UI
 */
function checkAuthStatus() {
    const token = localStorage.getItem("jwtToken");
    const loginLink = document.getElementById("loginLink");
    const registerLink = document.getElementById("registerLink");
    const profileLink = document.getElementById("profileLink");
    const logoutButton = document.getElementById("logoutButton");

    if (token) {
        console.log("🔒 Пользователь авторизован");

        if (loginLink) loginLink.style.display = "none";
        if (registerLink) registerLink.style.display = "none";
        if (profileLink) profileLink.style.display = "inline-block";
        if (logoutButton) logoutButton.style.display = "inline-block";
    } else {
        console.log("🔓 Пользователь не авторизован");

        if (profileLink) profileLink.style.display = "none";
        if (logoutButton) logoutButton.style.display = "none";
    }
}

/**
 * 📌 Обрабатывает процесс логина
 */
async function setupLogin() {
    const loginForm = document.getElementById("login-form");
    if (!loginForm) return;

    loginForm.addEventListener("submit", async function (event) {
        event.preventDefault();
        const usernameOrEmail = document.getElementById("username").value.trim();
        const password = document.getElementById("password").value;

        try {
            const response = await fetch(`${API_BASE_URL}/auth/login`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ username: usernameOrEmail, password })
            });

            if (!response.ok) throw new Error("Неверные учетные данные");

            const data = await response.json();
            localStorage.setItem("jwtToken", data.token);
            console.log("✅ Успешный вход, токен сохранен.");

            window.location.href = "index.html";
        } catch (error) {
            document.getElementById("login-message").innerHTML =
                `<div class="alert alert-danger">${error.message}</div>`;
        }
    });
}

/**
 * 📌 Обрабатывает процесс регистрации
 */
async function setupRegister() {
    const registerForm = document.getElementById("register-form");
    if (!registerForm) return;

    registerForm.addEventListener("submit", async function (event) {
        event.preventDefault();
        const registerButton = document.getElementById("register-button");
        registerButton.disabled = true;

        const username = document.getElementById("username").value.trim();
        const email = document.getElementById("email").value.trim();
        const password = document.getElementById("password").value;
        const passwordRepeat = document.getElementById("password-repeat").value;

        if (password !== passwordRepeat) {
            document.getElementById("register-message").innerHTML =
                `<div class="alert alert-danger">Пароли не совпадают</div>`;
            registerButton.disabled = false;
            return;
        }

        try {
            const response = await fetch(`${API_BASE_URL}/auth/register`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ username, email, password })
            });

            if (!response.ok) throw new Error("Ошибка регистрации");

            document.getElementById("register-message").innerHTML =
                `<div class="alert alert-success">Регистрация успешна! Теперь войдите.</div>`;
            registerForm.reset();
        } catch (error) {
            document.getElementById("register-message").innerHTML =
                `<div class="alert alert-danger">${error.message}</div>`;
        } finally {
            registerButton.disabled = false;
        }
    });
}

/**
 * 📌 Выход из аккаунта
 */
function logout() {
    localStorage.removeItem("jwtToken");
    console.log("🚪 Пользователь вышел из системы.");
    window.location.href = "login.html";
}
