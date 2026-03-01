/**
 * @file bookAppointment.js
 * @brief Handles customer interactions, API requests, and WebSocket connections for the booking station.
 */

// --- Event Listeners ---
document.getElementById('bookButton').addEventListener('click', bookAppointment);
document.getElementById('cancelButton').addEventListener('click', cancelAppointment);
document.getElementById('waitTimeButton').addEventListener('click', showWaitTime);
document.getElementById('positionButton').addEventListener('click', showPositionInQueue);

/**
 * @brief Automatically attempts to reconnect to WebSocket notifications on page load.
 * Checks the browser's sessionStorage to see if the user has an active appointment.
 */
window.onload = function() {
    const savedId = sessionStorage.getItem('appointmentId');
    if (savedId) {
        connectToNotifications(savedId);
    }
};

/**
 * @brief Establishes a WebSocket connection to listen for targeted notifications.
 * @param {string} id The unique UUID of the customer's appointment.
 */
function connectToNotifications(id) {
    // Initialize SockJS and STOMP over the /ws endpoint
    var socket = new SockJS('/ws');
    var stompClient = Stomp.over(socket);
    
    stompClient.connect({}, function () {
        // Subscribe to a unique topic specific to this user's ID
        stompClient.subscribe('/topic/notify/' + id, function (message) {
            alert(message.body); // Alerts the user when the employee clicks "Serve Next"
        });
    });
}

/**
 * @brief Gathers input data, sends a booking request to the server, and handles the response.
 * @async
 */
async function bookAppointment() {
    const nameInput = document.getElementById('name').value;
    const dateInput = document.getElementById('date').value;
    const hourInput = parseInt(document.getElementById('hour').value, 10);
    const statusMessage = document.getElementById('statusMessage');

    try {
        // Send POST request to the CustomerDashboard REST controller
        const response = await fetch('/api/customer/book', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name: nameInput, date: dateInput, hour: hourInput })
        });

        if (response.ok) {
            const bookedApp = await response.json(); 
            
            // Save the unique ID directly to the browser's session memory.
            // This allows the user to cancel or check their position later without needing to log in.
            sessionStorage.setItem('appointmentId', bookedApp.id); 
            
            statusMessage.textContent = "Booked! Waiting for your turn...";
            statusMessage.style.color = 'green';
            
            // Connect to the WebSocket so the server can ping this specific user
            connectToNotifications(bookedApp.id);
        } else {
            // Display validation error messages returned from the server (e.g., "Past dates not allowed")
            statusMessage.textContent = await response.text();
            statusMessage.style.color = 'red';
        }
    } catch (error) {
        statusMessage.textContent = 'Network error.';
        statusMessage.style.color = 'red';
    }
}

/**
 * @brief Cancels the user's active appointment based on the ID stored in their session.
 * @async
 */
async function cancelAppointment() {
    const statusMessage = document.getElementById('statusMessage');
    const savedId = sessionStorage.getItem('appointmentId');

    // Prevent cancellation if they haven't booked anything during this session
    if (!savedId) {
        statusMessage.textContent = "You don't have an active appointment to cancel.";
        statusMessage.style.color = 'red';
        return;
    }

    try {
        // Send DELETE request to the CustomerDashboard REST controller
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

/**
 * @brief Checks how many people are ahead of the current user in the global queue.
 * @async
 */
async function showPositionInQueue() {
    const statusMessage = document.getElementById('statusMessage');
    const savedId = sessionStorage.getItem('appointmentId');

    if (!savedId) {
        statusMessage.textContent = "You don't have an active appointment to check.";
        statusMessage.style.color = 'red';
        return;
    }

    try {
        // Send GET request with the UUID as a URL query parameter
        const response = await fetch(`/api/customer/position?id=${savedId}`);
        const responseText = await response.text();

        statusMessage.textContent = responseText;
        statusMessage.style.color = response.ok ? 'blue' : 'red';        
    } catch (error) {
        statusMessage.textContent = 'Network error.';
        statusMessage.style.color = 'red';
    }
}

/**
 * @brief Estimates the wait time based on the selected date and hour, and the current global duration setting.
 * @async
 */
async function showWaitTime() {
    const dateInput = document.getElementById('date').value;
    const hourInput = parseInt(document.getElementById('hour').value, 10);
    const statusMessage = document.getElementById('statusMessage');

    try {
        // Send GET request with date and hour as URL query parameters
        const response = await fetch(`/api/customer/wait-time?date=${dateInput}&hour=${hourInput}`);
        const responseText = await response.text();
        
        statusMessage.textContent = responseText;
        statusMessage.style.color = 'blue';
    } catch (error) {
        statusMessage.textContent = 'Network error.';
        statusMessage.style.color = 'red';
    }
}