const API_BASE_URL = "http://localhost:8080";

let currentPage = 0;
const pageSize = 12;
let hasNextPage = true;
let isLoading = false;

document.addEventListener("DOMContentLoaded", function () {
    checkUserAuth();
    setupLogin();
    setupRegister();
    document.getElementById("logoutButton")?.addEventListener("click", logout);

    if (document.getElementById("lot-grid")) {
        loadAuctionLots(currentPage, pageSize);
    }

    window.addEventListener("scroll", handleScroll);
});

/**
 * 📌 Проверяет аутентификацию пользователя и перенаправляет на login.html, если нет токена
 */
function checkUserAuth() {
    const token = localStorage.getItem("jwtToken");

    if (!token) {
        console.log("🔓 No token found, redirecting to login...");
        if (window.location.pathname !== "/login.html") {
            window.location.href = "login.html"; // ⏩ Перенаправляем на страницу входа
        }
    } else {
        console.log("🔒 User is authenticated");
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
        const usernameOrEmail = document.getElementById("usernameOrEmail").value.trim();
        const password = document.getElementById("password").value;

        try {
            const response = await fetch(`${API_BASE_URL}/auth/login`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ username: usernameOrEmail, password })
            });

            const data = await response.json();
            if (!response.ok) throw new Error(data.message || "Login error");

            // ✅ Сохраняем токен в localStorage
            localStorage.setItem("jwtToken", data.token);
            console.log("✅ Login successful, token saved.");

            // ⏩ Перенаправляем пользователя
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
function setupRegister() {
    const registerForm = document.getElementById("register-form");
    if (!registerForm) return;

    registerForm.addEventListener("submit", async function (event) {
        event.preventDefault();
        const registerButton = document.getElementById("register-button");
        registerButton.disabled = true;  // 🔥 Блокируем повторный клик

        const username = document.getElementById("username").value.trim();
        const email = document.getElementById("email").value.trim();
        const password = document.getElementById("password").value;
        const passwordRepeat = document.getElementById("password-repeat").value;

        if (password !== passwordRepeat) {
            document.getElementById("register-message").innerHTML =
                `<div class="alert alert-danger">Passwords do not match</div>`;
            registerButton.disabled = false;
            return;
        }

        try {
            const response = await fetch(`${API_BASE_URL}/auth/register`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ username, email, password })
            });

            const data = await response.json();
            if (!response.ok) throw new Error(data.message || "Registration error");

            document.getElementById("register-message").innerHTML =
                `<div class="alert alert-success">Registration successful! Now log in.</div>`;
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
    console.log("🚪 User logged out.");
    window.location.href = "login.html";
}

/**
 * 📌 Отправляет API-запрос с JWT-токеном
 */
async function apiRequest(endpoint, method = "GET", body = null, useAuth = true) {
    const headers = { "Content-Type": "application/json" };

    if (useAuth) {
        const token = localStorage.getItem("jwtToken");
        if (token) headers["Authorization"] = `Bearer ${token}`;
    }

    try {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            method,
            headers,
            credentials: "include",
            body: body ? JSON.stringify(body) : null
        });

        if (response.status === 403) {
            console.warn("⛔ Access forbidden! Check security settings.");
        }

        return response.ok ? response.json() : null;
    } catch (error) {
        console.error("❌ API request error:", error);
        return null;
    }
}

/**
 * 📌 Функция загрузки лотов с пагинацией
 */
function loadAuctionLots(page = 0, size = 12, reset = false) {
    if (isLoading || !hasNextPage) return;
    isLoading = true;
    console.log(`📡 Loading lots: page ${page}, size ${size}`);

    apiRequest(`/lots?page=${page}&size=${size}`)
        .then(data => {
            console.log("📦 Server data:", data);
            if (data && data.content && Array.isArray(data.content)) {
                displayAuctionLots(data.content, reset);
                currentPage++;
                hasNextPage = data.content.length === size;
            } else {
                hasNextPage = false;
            }
            isLoading = false;
            console.log(`✅ Next page: ${currentPage}, hasNextPage: ${hasNextPage}`);
        })
        .catch(error => {
            console.error("❌ Error loading lots:", error);
            isLoading = false;
        });
}

/**
 * 📌 Функция отображения лотов
 */
function displayAuctionLots(lots, reset = false) {
    const lotGrid = document.getElementById("lot-grid");
    if (reset) lotGrid.innerHTML = "";

    if (lots.length === 0) return;

    lots.forEach(lot => {
        const col = document.createElement("div");
        col.className = "col-md-4 mb-4";

        const card = document.createElement("div");
        card.className = "card h-100";

        const img = document.createElement("img");
        img.className = "card-img-top";
        img.src = lot.imageUrl && lot.imageUrl.trim() ? lot.imageUrl : "images/default.jpg";
        img.alt = lot.title;

        const cardBody = document.createElement("div");
        cardBody.className = "card-body";

        const cardTitle = document.createElement("h5");
        cardTitle.className = "card-title";
        cardTitle.textContent = lot.title;

        const priceText = document.createElement("p");
        priceText.className = "card-text";
        priceText.innerHTML = `<strong>Current Price:</strong> ${lot.currentPrice}₽`;

        const detailLink = document.createElement("a");
        detailLink.className = "btn btn-primary";
        detailLink.href = `auction.html?id=${lot.id}`;
        detailLink.textContent = "View Details";

        cardBody.append(cardTitle, priceText, detailLink);
        card.append(img, cardBody);
        col.append(card);
        lotGrid.append(col);
    });
}

/**
 * 📌 Обрабатывает прокрутку страницы
 */
function handleScroll() {
    if (isLoading || !hasNextPage) return;

    if (window.innerHeight + window.scrollY >= document.documentElement.scrollHeight - 100) {
        console.log("🔥 Scrolled to bottom, loading more lots...");
        loadAuctionLots(currentPage, pageSize);
    }
}

window.addEventListener("scroll", handleScroll);
