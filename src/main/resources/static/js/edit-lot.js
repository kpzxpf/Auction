document.addEventListener("DOMContentLoaded", async () => {
    const params = new URLSearchParams(window.location.search);
    const lotId = params.get("id");

    if (!lotId) {
        window.location.href = "profile.html";
        return;
    }

    await loadLotData(lotId);
    setupFormSubmission(lotId);
});

async function loadLotData(lotId) {
    const token = localStorage.getItem("jwtToken");
    if (!token) {
        window.location.href = "login.html";
        return;
    }

    try {
        const response = await fetch(`http://localhost:8080/lots/${lotId}`, {
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });
        if (!response.ok) throw new Error("Лот не найден");

        const lot = await response.json();
        populateForm(lot);
    } catch (error) {
        alert(error.message);
        window.location.href = "profile.html";
    }
}

function populateForm(lot) {
    document.getElementById("title").value = lot.title;
    document.getElementById("description").value = lot.description;
    document.getElementById("startingPrice").value = lot.startingPrice;
    document.getElementById("startTime").value = lot.startTime;
    document.getElementById("endTime").value = lot.endTime;
}

function setupFormSubmission(lotId) {
    document.getElementById("edit-lot-form").addEventListener("submit", async (e) => {
        e.preventDefault();

        const token = localStorage.getItem("jwtToken");
        if (!token) {
            window.location.href = "login.html";
            return;
        }

        const formData = {
            id: lotId, // Добавляем ID лота в тело запроса
            title: document.getElementById("title").value,
            description: document.getElementById("description").value,
            startingPrice: document.getElementById("startingPrice").value,
            startTime: document.getElementById("startTime").value,
            endTime: document.getElementById("endTime").value
        };

        try {
            const response = await fetch(`http://localhost:8080/lots`, {
                method: "PUT",
                headers: {
                    "Authorization": `Bearer ${token}`,
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(formData)
            });
            if (!response.ok) throw new Error("Ошибка обновления лота");

            alert("Лот успешно обновлен!");
            window.location.href = "profile.html";
        } catch (error) {
            console.error(error);
            alert("Ошибка при обновлении лота");
        }
    });
}