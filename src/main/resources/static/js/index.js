let currentPage = 0;
let isLoading = false;

document.addEventListener("DOMContentLoaded", () => {
    loadAuctionLots();
    setupCategoryDropdown();
    setupInfiniteScroll();


    const profileLink = document.getElementById("profileLink");
    const logoutButton = document.getElementById("logoutButton");
    const loginLink = document.getElementById("loginLink");
    const registerLink = document.getElementById("registerLink");

    function updateNavigation() {
        const token = localStorage.getItem("jwtToken");
        if (token) {
            profileLink.style.display = "block";
            logoutButton.style.display = "block";
            loginLink.style.display = "none";
            registerLink.style.display = "none";
        } else {
            profileLink.style.display = "none";
            logoutButton.style.display = "none";
            loginLink.style.display = "block";
            registerLink.style.display = "block";
        }
    }

    updateNavigation();


    logoutButton.addEventListener("click", () => {
        localStorage.removeItem("jwtToken");
        updateNavigation();
    });


    profileLink.addEventListener("click", (event) => {
        if (!token) {
            event.preventDefault();
        }
    });
});

async function loadAuctionLots(category = "all", page = 0) {
    if (isLoading) return;
    isLoading = true;

    try {
        const response = await fetch(`http://localhost:8080/lots?category=${category}&page=${page}&size=9`);
        const lots = await response.json();

        if (lots.length > 0) {
            displayAuctionLots(lots);
            currentPage = page;
        } else {
            console.log("Больше лотов нет.");
        }
    } catch (error) {
        console.error("❌ Error loading auction lots:", error);
    } finally {
        isLoading = false;
    }
}

function displayAuctionLots(lots) {
    const lotGrid = document.getElementById("lot-grid");

    if (lots.length === 0 && currentPage === 0) {
        lotGrid.innerHTML = "<p class='text-center'>Нет доступных лотов.</p>";
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
                    <a href="lot.html?id=${lot.id}" class="btn btn-primary">Подробнее</a>
                </div>
            </div>
        `;
        lotGrid.appendChild(lotElement);
    });
}

function setupCategoryDropdown() {
    document.querySelectorAll(".category-menu a").forEach(link => {
        link.addEventListener("click", (event) => {
            event.preventDefault();
            const category = event.target.dataset.category;
            currentPage = 0;
            document.getElementById("lot-grid").innerHTML = "";
            loadAuctionLots(category, 0);
        });
    });
}

function setupInfiniteScroll() {
    window.addEventListener("scroll", () => {
        const { scrollTop, scrollHeight, clientHeight } = document.documentElement;

        if (scrollTop + clientHeight >= scrollHeight - 10 && !isLoading) {
            loadAuctionLots("all", currentPage + 1);
        }
    });
}

