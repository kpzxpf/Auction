document.addEventListener("DOMContentLoaded", () => {
    loadUserBids();
});

async function loadUserBids() {
    const token = localStorage.getItem("jwtToken");
    if (!token) {
        window.location.href = "login.html";
        return;
    }

    try {
        const response = await fetch("http://localhost:8080/bids/user", {
            headers: { "Authorization": `Bearer ${token}` }
        });
        if (!response.ok) throw new Error("Ошибка загрузки ставок");

        const bids = await response.json();
        displayUserBids(bids);
    } catch (error) {
        console.error(error);
    }
}

function displayUserBids(bids) {
    const container = document.getElementById("bids-list");
    container.innerHTML = "";

    if (bids.length === 0) {
        container.innerHTML = "<p class='text-center'>Вы еще не сделали ставок.</p>";
        return;
    }

    bids.forEach(bid => {
        const bidElement = document.createElement("div");
        bidElement.className = "col-md-4 mb-4";
        bidElement.innerHTML = `
            <div class="card">
                <div class="card-body">
                    <h5 class="card-title">Лот: ${bid.lotTitle}</h5>
                    <p class="card-text"><strong>Ставка:</strong> ${bid.amount}₽</p>
                    <p class="card-text"><strong>Дата:</strong> ${new Date(bid.timestamp).toLocaleString()}</p>
                </div>
            </div>
        `;
        container.appendChild(bidElement);
    });
}
