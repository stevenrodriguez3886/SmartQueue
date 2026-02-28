// Attach the functions to the HTML buttons
document.getElementById('bookButton').addEventListener('click', bookAppointment);
document.getElementById('cancelButton').addEventListener('click', cancelAppointment);
document.getElementById('waitTimeButton').addEventListener('click', showWaitTime);
document.getElementById('positionButton').addEventListener('click', showPositionInQueue);

async function bookAppointment() {
    const nameInput = document.getElementById('name').value;
    const dateInput = document.getElementById('date').value;
    const hourInput = parseInt(document.getElementById('hour').value, 10);
    const statusMessage = document.getElementById('statusMessage');

    const appointmentRequest = { name: nameInput, date: dateInput, hour: hourInput };

    try {
        const response = await fetch('/api/customer/book', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(appointmentRequest)
        });

        const responseText = await response.text();
        statusMessage.textContent = responseText;
        statusMessage.style.color = response.ok ? 'green' : 'red';
        
    } catch (error) {
        statusMessage.textContent = 'Network error.';
    }
}

async function cancelAppointment() {
    const nameInput = document.getElementById('name').value;
    const dateInput = document.getElementById('date').value;
    const hourInput = parseInt(document.getElementById('hour').value, 10);
    const statusMessage = document.getElementById('statusMessage');

    try {
        const response = await fetch('/api/customer/cancel', {
            method: 'DELETE',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name: nameInput, date: dateInput, hour: hourInput })
        });

        const responseText = await response.text();
        statusMessage.textContent = responseText;
        statusMessage.style.color = response.ok ? 'green' : 'red';
    } catch (error) {
        statusMessage.textContent = 'Network error.';
    }
}

async function showWaitTime() {
    const dateInput = document.getElementById('date').value;
    const hourInput = parseInt(document.getElementById('hour').value, 10);
    const statusMessage = document.getElementById('statusMessage');

    try {
        const response = await fetch(`/api/customer/wait-time?date=${dateInput}&hour=${hourInput}`);
        const responseText = await response.text();
        statusMessage.textContent = responseText;
        statusMessage.style.color = 'blue';
    } catch (error) {
        statusMessage.textContent = 'Network error.';
    }
}

async function showPositionInQueue() {
    const queueTableBody = document.getElementById('queueTableBody');
    try {
        const response = await fetch('/api/customer/queue');
        if (response.ok) {
            const appointments = await response.json();
            queueTableBody.innerHTML = ''; 
            appointments.forEach(app => {
                const row = `<tr><td>${app.date}</td><td>${app.hour}:00</td></tr>`;
                queueTableBody.innerHTML += row;
            });
            document.getElementById('queueContainer').style.display = 'block';
        }
    } catch (error) {
        console.error("Queue error");
    }
}