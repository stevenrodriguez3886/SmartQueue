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

        if (response.ok) {
            const bookedApp = await response.json(); // Get the Appointment object back
            const myId = bookedApp.id; 
            statusMessage.textContent = "Booked! Waiting for your turn...";
            statusMessage.style.color = 'green';
            
            // Connect to WebSocket using this unique ID
            var socket = new SockJS('/ws');
            var stompClient = Stomp.over(socket);
            stompClient.connect({}, function (frame) {
                stompClient.subscribe('/topic/notify/' + myId, function (message) {
                    alert(message.body); // Only this specific browser gets the alert
                });
            });
        }
        
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
    const nameInput = document.getElementById('name').value;
    const dateInput = document.getElementById('date').value;
    const hourInput = parseInt(document.getElementById('hour').value, 10);
    const statusMessage = document.getElementById('statusMessage');

    try {
        // Fetch the position using the specific customer's details
        const response = await fetch(`/api/customer/position?name=${nameInput}&date=${dateInput}&hour=${hourInput}`);
        const responseText = await response.text();

        // Update the UI
        statusMessage.textContent = responseText;
        statusMessage.style.color = response.ok ? 'blue' : 'red';
        
        // Hide the old table container since we are using a text message now
        document.getElementById('queueContainer').style.display = 'none';
        
    } catch (error) {
        statusMessage.textContent = 'Network error.';
        statusMessage.style.color = 'red';
    }
}