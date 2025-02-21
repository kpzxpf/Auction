document.addEventListener("DOMContentLoaded", () => {
    loadUserProfile();
    loadUserLots();
});

async function loadUserProfile() {
    const token = localStorage.getItem("jwtToken");
    if (!token) {
        window.location.href = "login.html";
        return;
    }

    try {
        const response = await fetch("http://localhost:8080/user/profile", {
            headers: { "Authorization": `Bearer ${token}` }
        });
        if (!response.ok) throw new Error("Ошибка загрузки профиля");

        const user = await response.json();
        document.getElementById("profile-name").textContent = user.username;
        document.getElementById("profile-balance").textContent = `${user.balance || 0}₽`;
    } catch (error) {
        console.error(error);
        window.location.href = "login.html";
    }
}

async function loadUserLots() {
    const token = localStorage.getItem("jwtToken");
    if (!token) return;

    try {
        const response = await fetch("http://localhost:8080/lots/user", {
            headers: { "Authorization": `Bearer ${token}` }
        });
        if (!response.ok) throw new Error("Ошибка загрузки лотов");

        const lots = await response.json();
        displayUserLots(lots);
    } catch (error) {
        console.error(error);
    }
}

function displayUserLots(lots) {
    const container = document.getElementById("user-lots");
    container.innerHTML = "";

    if (lots.length === 0) {
        container.innerHTML = "<p class='text-center'>Нет ваших лотов.</p>";
        return;
    }

    lots.forEach(lot => {
        const lotElement = document.createElement("div");
        lotElement.className = "col-md-4 mb-4";
        lotElement.innerHTML = `
            <div class="card">
                <img src="${lot.imageUrl || 'images/default.jpg'}" class="card-img-top" alt="${lot.title}">
                <div class="card-body">
                    <h5 class="card-title">${lot.title}</h5>
                    <p class="card-text"><strong>Цена:</strong> ${lot.currentPrice}₽</p>
                    <a href="auction.html?id=${lot.id}" class="btn btn-primary">Подробнее</a>
                </div>
            </div>
        `;
        container.appendChild(lotElement);
    });
}
