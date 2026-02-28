// Connect to the WebSocket for live updates
var socket = new SockJS('/ws');
var stompClient = Stomp.over(socket);

stompClient.connect({}, function (frame) {
    // Listen to the public update channel
    stompClient.subscribe('/topic/queue-update', function (message) {
        // Automatically fetch the newest list whenever anyone books, cancels, or is served
        loadFullQueue(); 
    });
});

// Attach event listeners to the buttons in employee.html
document.getElementById('refreshButton').addEventListener('click', loadFullQueue);
document.getElementById('serveButton').addEventListener('click', serveNextCustomer);
document.getElementById('updateDurationButton').addEventListener('click', updateDuration);

// Load the queue immediately when the page opens
window.onload = loadFullQueue;

/**
 * Fetches the full list of appointments from the EmployeeDashboard and displays them in the table.
 */
async function loadFullQueue() {
    const tableBody = document.getElementById('employeeQueueBody');
    try {
        // Calls @GetMapping("/full-queue") in EmployeeDashboard.java
        const response = await fetch('/api/employee/full-queue');
        
        if (response.ok) {
            const appointments = await response.json();
            tableBody.innerHTML = ''; // Clear existing rows

            appointments.forEach(app => {
                const row = document.createElement('tr');
                // Format the hour to HH:00
                const formattedTime = app.hour < 10 ? `0${app.hour}:00` : `${app.hour}:00`;
                
                row.innerHTML = `
                    <td>${app.name}</td>
                    <td>${app.date}</td>
                    <td>${formattedTime}</td>
                `;
                tableBody.appendChild(row);
            });
        } else if (response.status === 403) {
            alert("Session expired or unauthorized. Please refresh and log in again.");
        }
    } catch (error) {
        console.error("Error loading queue:", error);
    }
}

/**
 * Removes the first person from the queue and updates the display.
 */
async function serveNextCustomer() {
    const statusDiv = document.getElementById('serveStatus');
    try {
        // Calls @DeleteMapping("/serve") in EmployeeDashboard.java
        const response = await fetch('/api/employee/serve', { method: 'DELETE' });
        const resultText = await response.text();

        if (response.ok) {
            statusDiv.style.backgroundColor = '#d1f2eb';
            statusDiv.style.color = '#1b4f72';
            statusDiv.textContent = resultText; // Displays "Now Serving: [Name]"
            loadFullQueue(); // Refresh the table automatically
        } else {
            statusDiv.style.backgroundColor = '#fadbd8';
            statusDiv.style.color = '#78281f';
            statusDiv.textContent = resultText; // Displays "Queue is empty"
        }
    } catch (error) {
        statusDiv.textContent = "Network error: Could not reach the server.";
    }
}

/**
 * Updates the global appointment duration used for wait-time calculations.
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
            
            // Fade out the success message after 3 seconds
            setTimeout(() => { statusSpan.textContent = ''; }, 3000);
        } else {
            statusSpan.style.color = '#c0392b';
            statusSpan.textContent = '✕ Update failed';
        }
    } catch (error) {
        statusSpan.textContent = 'Connection error';
    }
}