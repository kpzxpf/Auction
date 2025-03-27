document.addEventListener("DOMContentLoaded", async () => {
    const token = localStorage.getItem("jwtToken");
    const logoutButton = document.getElementById("logoutButton");

    if (!token) {
        window.location.href = "login.html";
        return;
    }

    logoutButton.addEventListener("click", () => {
        localStorage.removeItem("jwtToken");
        window.location.href = "index.html";
    });

    const addLotButton = document.getElementById("add-lot-btn");
    addLotButton.addEventListener("click", (event) => {
        event.preventDefault();
        if (currentUserId) {
            window.location.href = `add-lot.html?seller_id=${currentUserId}`;
        } else {
            alert("ID пользователя не загружен. Попробуйте обновить страницу.");
        }
    });

    await loadUserProfile();
    await loadUserLots();
});

let currentUserId = null;

async function loadUserProfile() {
    try {
        const response = await fetch("http://localhost:8080/user/profile", {
            headers: { "Authorization": `Bearer ${localStorage.getItem("jwtToken")}` }
        });
        if (!response.ok) throw new Error("Ошибка загрузки профиля");

        const user = await response.json();
        currentUserId = user.id;

        const profileUsername = document.getElementById("profile-username");
        const profileEmail = document.getElementById("profile-email");
        const profileBalance = document.getElementById("profile-balance");

        if (profileUsername && profileEmail && profileBalance) {
            profileUsername.textContent = user.username;
            profileEmail.textContent = user.email;
            profileBalance.textContent = `${user.balance || 0}₽`;
        } else {
            console.error("Элементы профиля не найдены в DOM");
        }
    } catch (error) {
        console.error(error);
    }
}

async function loadUserLots() {
    if (!currentUserId) {
        console.error("ID пользователя не найден");
        return;
    }

    try {
        const response = await fetch(`http://localhost:8080/lots/user/${currentUserId}`, {
            headers: { "Authorization": `Bearer ${localStorage.getItem("jwtToken")}` }
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
    if (!container) {
        console.error("Элемент 'user-lots' не найден в DOM");
        return;
    }

    container.innerHTML = "";

    if (lots.length === 0) {
        container.innerHTML = "<p class='text-center'>Нет ваших лотов.</p>";
        return;
    }

    lots.forEach(lot => {
        const lotElement = document.createElement("div");
        lotElement.className = "col-md-4 mb-4";
        lotElement.innerHTML = `
            <div class="card h-100 shadow-sm">
                <img src="${lot.images?.[0]?.url || 'images/banner.jpg'}" 
                     class="card-img-top" 
                     alt="${lot.title}"
                     style="height: 200px; object-fit: cover;">
                <div class="card-body">
                    <h5 class="card-title">${lot.title}</h5>
                    <p class="card-text text-muted">${lot.description || ''}</p>
                    <div class="d-flex justify-content-between align-items-center">
                        <span class="badge bg-primary">${lot.categoryName || 'Без категории'}</span>
                        <h5 class="text-success">${lot.currentPrice.toFixed(2)}₽</h5>
                    </div>
                    <div class="mt-2">
                        <a href="lot.html?id=${lot.id}" class="btn btn-primary btn-sm">
                            <i class="fas fa-eye"></i> Подробнее
                        </a>
                        <a href="edit-lot.html?id=${lot.id}" class="btn btn-warning btn-sm">
                            <i class="fas fa-edit"></i> Редактировать
                        </a>
                    </div>
                </div>
                <div class="card-footer bg-transparent">
                    <small class="text-muted">
                        Окончание: ${new Date(lot.endTime).toLocaleDateString()}
                    </small>
                </div>
            </div>
        `;
        container.appendChild(lotElement);
    });
}