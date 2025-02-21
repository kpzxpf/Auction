document.addEventListener("DOMContentLoaded", () => {
    loadAuctionLots();
    setupCategoryDropdown();
});

async function loadAuctionLots(category = "all") {
    try {
        const response = await fetch(`http://localhost:8080/lots?category=${category}`);
        const lots = await response.json();
        displayAuctionLots(lots);
    } catch (error) {
        console.error("❌ Error loading auction lots:", error);
    }
}

function displayAuctionLots(lots) {
    const lotGrid = document.getElementById("lot-grid");
    lotGrid.innerHTML = "";

    if (lots.length === 0) {
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
                    <a href="auction.html?id=${lot.id}" class="btn btn-primary">Подробнее</a>
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
            loadAuctionLots(category);
        });
    });
}
