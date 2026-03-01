document.getElementById('bookButton').addEventListener('click', bookAppointment);
document.getElementById('cancelButton').addEventListener('click', cancelAppointment);
document.getElementById('waitTimeButton').addEventListener('click', showWaitTime);
document.getElementById('positionButton').addEventListener('click', showPositionInQueue);

// Automatically reconnect to notifications if you refresh the page
window.onload = function() {
    const savedId = sessionStorage.getItem('appointmentId');
    if (savedId) {
        connectToNotifications(savedId);
    }
};

function connectToNotifications(id) {
    var socket = new SockJS('/ws');
    var stompClient = Stomp.over(socket);
    stompClient.connect({}, function () {
        stompClient.subscribe('/topic/notify/' + id, function (message) {
            alert(message.body); 
        });
    });
}

async function bookAppointment() {
    const nameInput = document.getElementById('name').value;
    const dateInput = document.getElementById('date').value;
    const hourInput = parseInt(document.getElementById('hour').value, 10);
    const statusMessage = document.getElementById('statusMessage');

    try {
        const response = await fetch('/api/customer/book', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name: nameInput, date: dateInput, hour: hourInput })
        });

        if (response.ok) {
            const bookedApp = await response.json(); 
            
            // Save the unique ID directly to the browser's memory
            sessionStorage.setItem('appointmentId', bookedApp.id); 
            
            statusMessage.textContent = "Booked! Waiting for your turn...";
            statusMessage.style.color = 'green';
            
            connectToNotifications(bookedApp.id);
        } else {
            statusMessage.textContent = await response.text();
            statusMessage.style.color = 'red';
        }
    } catch (error) {
        statusMessage.textContent = 'Network error.';
        statusMessage.style.color = 'red';
    }
}

async function cancelAppointment() {
    const statusMessage = document.getElementById('statusMessage');
    const savedId = sessionStorage.getItem('appointmentId');

    if (!savedId) {
        statusMessage.textContent = "You don't have an active appointment to cancel.";
        statusMessage.style.color = 'red';
        return;
    }

    try {
        const response = await fetch('/api/customer/cancel', {
            method: 'DELETE',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ id: savedId }) 
        });

        const responseText = await response.text();
        statusMessage.textContent = responseText;
        statusMessage.style.color = response.ok ? 'green' : 'red';
        
        if (response.ok) {
            sessionStorage.removeItem('appointmentId'); // Clear the ID on success
        }
    } catch (error) {
        statusMessage.textContent = 'Network error.';
        statusMessage.style.color = 'red';
    }
}

async function showPositionInQueue() {
    const statusMessage = document.getElementById('statusMessage');
    const savedId = sessionStorage.getItem('appointmentId');

    if (!savedId) {
        statusMessage.textContent = "You don't have an active appointment to check.";
        statusMessage.style.color = 'red';
        return;
    }

    try {
        const response = await fetch(`/api/customer/position?id=${savedId}`);
        const responseText = await response.text();

        statusMessage.textContent = responseText;
        statusMessage.style.color = response.ok ? 'blue' : 'red';        
    } catch (error) {
        statusMessage.textContent = 'Network error.';
        statusMessage.style.color = 'red';
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
        statusMessage.style.color = 'red';
    }
}