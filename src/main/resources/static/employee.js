/**
 * @file employee.js
 * @brief Handles API calls and live WebSocket updates for the staff dashboard.
 */

// --- WebSocket Setup ---
// Connect to the WebSocket endpoint for live global updates
var socket = new SockJS('/ws');
var stompClient = Stomp.over(socket);

stompClient.connect({}, function (frame) {
    // Listen to the public update channel for broad queue changes
    stompClient.subscribe('/topic/queue-update', function (message) {
        // Automatically fetch the newest list from the DB whenever anyone books, cancels, or is served.
        // This ensures all staff dashboards are always in sync.
        loadFullQueue(); 
    });
});

// --- Event Listeners ---
document.getElementById('refreshButton').addEventListener('click', loadFullQueue);
document.getElementById('serveButton').addEventListener('click', serveNextCustomer);
document.getElementById('updateDurationButton').addEventListener('click', updateDuration);
// NEW listener for the hours button
document.getElementById('updateHoursButton').addEventListener('click', updateServiceHours);

// Load the queue immediately when the page finishes loading
window.onload = loadFullQueue;

/**
 * @brief Fetches the full list of appointments from the server and populates the HTML table.
 * @async
 */
async function loadFullQueue() {
    const tableBody = document.getElementById('employeeQueueBody');
    try {
        // Calls @GetMapping("/full-queue") in EmployeeDashboard.java
        const response = await fetch('/api/employee/full-queue');
        
        if (response.ok) {
            const appointments = await response.json();
            tableBody.innerHTML = ''; // Clear existing rows before appending new ones

            // Iterate through the JSON array and build table rows dynamically
            appointments.forEach(app => {
                const row = document.createElement('tr');
                // Format the 24-hour integer to a more readable HH:00 string format
                const formattedTime = app.hour < 10 ? `0${app.hour}:00` : `${app.hour}:00`;
                
                row.innerHTML = `
                    <td>${app.name}</td>
                    <td>${app.date}</td>
                    <td>${formattedTime}</td>
                `;
                tableBody.appendChild(row);
            });
        } else if (response.status === 403) {
            // Spring Security returns 403 Forbidden if the staff user isn't logged in
            alert("Session expired or unauthorized. Please refresh and log in again.");
        }
    } catch (error) {
        console.error("Error loading queue:", error);
    }
}

/**
 * @brief Triggers the server to remove the first person in line and notify them.
 * @async
 */
async function serveNextCustomer() {
    const statusDiv = document.getElementById('serveStatus');
    try {
        // Calls @DeleteMapping("/serve") in EmployeeDashboard.java
        const response = await fetch('/api/employee/serve', { method: 'DELETE' });
        const resultText = await response.text();

        if (response.ok) {
            // Success styling (Green-ish)
            statusDiv.style.backgroundColor = '#d1f2eb';
            statusDiv.style.color = '#1b4f72';
            statusDiv.textContent = resultText; // Displays "Now Serving: [Name]"
            loadFullQueue(); // Refresh the table automatically
        } else {
            // Error styling (Red-ish)
            statusDiv.style.backgroundColor = '#fadbd8';
            statusDiv.style.color = '#78281f';
            statusDiv.textContent = resultText; // Displays "Queue is empty"
        }
    } catch (error) {
        statusDiv.textContent = "Network error: Could not reach the server.";
    }
}

/**
 * @brief Updates the global appointment duration used by the backend for wait-time estimations.
 * @async
 */
async function updateDuration() {
    const minutes = document.getElementById('durationInput').value;
    const statusSpan = document.getElementById('durationStatus');

    try {
        // Calls @PostMapping("/duration") in EmployeeDashboard.java
        const response = await fetch(`/api/employee/duration?minutes=${minutes}`, {
            method: 'POST'
        });

        if (response.ok) {
            statusSpan.style.color = '#27ae60';
            statusSpan.textContent = `✓ Set to ${minutes}m`;
            
            // Fade out the success message after 3 seconds so the UI stays clean
            setTimeout(() => { statusSpan.textContent = ''; }, 3000);
        } else {
            statusSpan.style.color = '#c0392b';
            statusSpan.textContent = '✕ Update failed';
        }
    } catch (error) {
        statusSpan.textContent = 'Connection error';
    }
}

/**
 * @brief Updates the global service hours allowing/restricting customer bookings.
 * @async
 */
async function updateServiceHours() {
    const openHour = document.getElementById('openHourInput').value;
    const closeHour = document.getElementById('closeHourInput').value;
    const statusSpan = document.getElementById('hoursStatus');

    try {
        // Calls the new @PostMapping("/hours") in EmployeeDashboard.java
        const response = await fetch(`/api/employee/hours?openHour=${openHour}&closeHour=${closeHour}`, { 
            method: 'POST' 
        });
        const resultText = await response.text();

        if (response.ok) {
            statusSpan.style.color = '#27ae60';
            statusSpan.textContent = `✓ ${resultText}`;
            setTimeout(() => { statusSpan.textContent = ''; }, 4000);
        } else {
            statusSpan.style.color = '#c0392b';
            statusSpan.textContent = `✕ ${resultText}`; // Shows validation error (e.g. Open must be before Close)
        }
    } catch (error) {
        statusSpan.textContent = 'Connection error';
    }
}