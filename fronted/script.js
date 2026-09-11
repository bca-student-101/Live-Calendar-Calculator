// Live Calendar & Calculator
const API_BASE_URL = "https://live-calendar-calculator.onrender.com";
let currentInput = "";
let currentYear = new Date().getFullYear();
let currentMonth = new Date().getMonth() + 1; // 1-indexed (1 = Jan, 12 = Dec)
let activeMode = "solar"; // "solar" or "lunar"

// ==========================================
// 1. CALCULATOR LOGIC
// ==========================================
const display = document.getElementById("display");
const buttons = document.querySelectorAll(".calculator-buttons button");

buttons.forEach(button => {
    button.addEventListener("click", async () => {
        const value = button.textContent;

        // Clear display
        if (value === "C") {
            currentInput = "";
            display.value = "";
            return;
        }

        // Evaluate expression
        if (value === "=") {
            if (!currentInput) return;
            try {
                let operation = "";
                if (currentInput.includes("+")) operation = "add";
                else if (currentInput.includes("-")) operation = "subtract";
                else if (currentInput.includes("*")) operation = "multiply";
                else if (currentInput.includes("/")) operation = "divide";

                if (operation) {
                    const operatorChar = operation === "add" ? "+" :
                        operation === "subtract" ? "-" :
                        operation === "multiply" ? "*" : "/";
                    const parts = currentInput.split(operatorChar);
                    const a = Number(parts[0]);
                    const b = Number(parts[1]);

                    if (!isNaN(a) && !isNaN(b)) {
                        try {
                            const response = await fetch(
                                `${API_BASE_URL}/api/calculator?a=${a}&b=${b}&operation=${operation}`
                            );
                            const data = await response.json();
                            display.value = data.result;
                            currentInput = String(data.result);
                            return;
                        } catch (err) {
                            // Local fallback calculation if backend is unavailable
                            let res = 0;
                            if (operation === "add") res = a + b;
                            else if (operation === "subtract") res = a - b;
                            else if (operation === "multiply") res = a * b;
                            else if (operation === "divide") res = b !== 0 ? a / b : "Error";
                            display.value = res;
                            currentInput = String(res);
                            return;
                        }
                    }
                }
                // Standard JS eval fallback for general expressions
                const evalResult = Function(`'use strict'; return (${currentInput})`)();
                display.value = evalResult;
                currentInput = String(evalResult);
            } catch (error) {
                display.value = "Error";
                currentInput = "";
            }
            return;
        }

        // Add digit/operator
        currentInput += value;
        display.value = currentInput;
    });
});

// ==========================================
// 2. CALENDAR LOGIC
// ==========================================

// Render Calendar Days
async function loadCalendarDays(year, month) {
    const calendarDays = document.getElementById("calendar-days");
    if (!calendarDays) return;
    calendarDays.innerHTML = "";

    // Update Header Text (e.g. "2026 - 9")
    const monthYearHeading = document.getElementById("month-year");
    if (monthYearHeading) {
        monthYearHeading.textContent = `${year} - ${month}`;
    }

    let daysInMonth = new Date(year, month, 0).getDate();
    let firstDayIndex = new Date(year, month - 1, 1).getDay(); // 0 = Sunday, 1 = Monday ... 6 = Saturday

    // Try fetching month info and first day from backend
    try {
        const [monthRes, firstDayRes] = await Promise.all([
            fetch(`${API_BASE_URL}/calendar/month?year=${year}&month=${month}`),
            fetch(`${API_BASE_URL}/api/calendar/first-day?year=${year}&month=${month}`)
        ]);
        if (monthRes.ok) {
            const mData = await monthRes.json();
            if (mData.days) daysInMonth = mData.days;
        }
        if (firstDayRes.ok) {
            const fData = await firstDayRes.json();
            // Java DayOfWeek: 1=Mon, 2=Tue...7=Sun. In Sun-indexed grid: 7%7=0 (Sun), 1=Mon...
            if (fData.firstDay !== undefined) {
                firstDayIndex = fData.firstDay % 7;
            }
        }
    } catch (e) {
        console.log("Using client-side date calculation (backend offline or loading)");
    }

    // 1. Add empty placeholder spans for alignment before day 1
    for (let i = 0; i < firstDayIndex; i++) {
        const emptyElement = document.createElement("span");
        emptyElement.className = "empty-day";
        calendarDays.appendChild(emptyElement);
    }

    const today = new Date();

    // 2. Add clickable date spans for each day of the month
    for (let day = 1; day <= daysInMonth; day++) {
        const dateElement = document.createElement("span");
        dateElement.textContent = day;
        dateElement.className = "calendar-day-btn";

        // Day of week tooltip calculation (instant local result)
        const dateObj = new Date(year, month - 1, day);
        const dayName = dateObj.toLocaleDateString("en-US", { weekday: "long" });
        dateElement.title = `${dayName}, ${month}/${day}/${year}`;

        // Highlight today's date
        if (year === today.getFullYear() && month === (today.getMonth() + 1) && day === today.getDate()) {
            dateElement.classList.add("today");
        }

        // CLICK EVENT LISTENER ON DATE SPAN
        dateElement.addEventListener("click", async () => {
            // Remove 'selected' class from previously selected date
            document.querySelectorAll("#calendar-days .calendar-day-btn").forEach(el => el.classList.remove("selected"));
            dateElement.classList.add("selected");

            // PROMINENT CONSOLE LOGS
            console.log(`========================================`);
            console.log(`📅 CALENDAR DATE CLICKED!`);
            console.log(`Day: ${day}, Month: ${month}, Year: ${year}`);
            console.log(`Day of Week: ${dayName}`);
            console.log(`========================================`);

            // Fetch festival data from backend
            const festivalList = document.getElementById("festival-list");
            if (festivalList) {
                festivalList.textContent = "Loading festival info...";
            }

            try {
                const response = await fetch(
                    `${API_BASE_URL}/api/festival?year=${year}&month=${month}&day=${day}`
                );
                if (response.ok) {
                    const data = await response.json();
                    console.log(`🎉 Festival Info for ${day}/${month}/${year}:`, data.festival);
                    if (festivalList) {
                        festivalList.textContent = `Date: ${day}/${month}/${year} - ${data.festival}`;
                    }
                } else {
                    throw new Error("Festival API error");
                }
            } catch (error) {
                console.log(`⚠️ Festival fetch fallback for ${day}/${month}/${year}`);
                // Basic fallback for standard holidays
                let fest = "No Festival Today";
                if (month === 1 && day === 26) fest = "Republic Day 🇮🇳";
                else if (month === 8 && day === 15) fest = "Independence Day 🇮🇳";
                else if (month === 10 && day === 2) fest = "Gandhi Jayanti 🇮🇳";
                else if (month === 12 && day === 25) fest = "Christmas 🎄";

                if (festivalList) {
                    festivalList.textContent = `Date: ${day}/${month}/${year} - ${fest}`;
                }
            }
        });

        calendarDays.appendChild(dateElement);
    }

    console.log(`Calendar loaded for ${year}-${month} (${daysInMonth} days, first day offset: ${firstDayIndex})`);
}

// Initial Live Data Load
async function updateDateTime() {
    try {
        const response = await fetch("${API_BASE_URL}/api/calendar");
        if (response.ok) {
            const data = await response.json();
            if (data.date) {
                const curDateEl = document.getElementById("current-date");
                if (curDateEl) curDateEl.textContent = data.date;
            }
            if (data.year && data.month) {
                currentYear = Number(data.year);
                currentMonth = Number(data.month);
            }
            if (data.lunarDate) {
                const lunarEl = document.getElementById("lunar-date");
                if (lunarEl) lunarEl.textContent = data.lunarDate;
            }
        }
    } catch (error) {
        console.log("Backend offline on initial load, using local date");
        const today = new Date();
        const curDateEl = document.getElementById("current-date");
        if (curDateEl) curDateEl.textContent = today.toISOString().split("T")[0];
    }

    // Render the calendar days
    await loadCalendarDays(currentYear, currentMonth);
}

// Next / Previous Month Navigation
const previousMonthButton = document.getElementById("previous-month");
const nextMonthButton = document.getElementById("next-month");

if (previousMonthButton) {
    previousMonthButton.addEventListener("click", async () => {
        currentMonth--;
        if (currentMonth === 0) {
            currentMonth = 12;
            currentYear--;
        }
        console.log(`Navigated to Previous Month: ${currentYear}-${currentMonth}`);
        await loadCalendarDays(currentYear, currentMonth);
    });
}

if (nextMonthButton) {
    nextMonthButton.addEventListener("click", async () => {
        currentMonth++;
        if (currentMonth === 13) {
            currentMonth = 1;
            currentYear++;
        }
        console.log(`Navigated to Next Month: ${currentYear}-${currentMonth}`);
        await loadCalendarDays(currentYear, currentMonth);
    });
}

// Solar / Lunar Calendar Mode Buttons
const solarBtn = document.getElementById("solar");
const lunarBtn = document.getElementById("lunar");
const lunarDateText = document.getElementById("lunar-date");

if (solarBtn) {
    solarBtn.addEventListener("click", () => {
        activeMode = "solar";
        solarBtn.style.background = "#2563eb";
        solarBtn.style.color = "white";
        if (lunarBtn) {
            lunarBtn.style.background = "#e5e7eb";
            lunarBtn.style.color = "black";
        }
        if (lunarDateText) {
            lunarDateText.textContent = "Solar Calendar Mode Active";
        }
        console.log("☀️ Switched to Solar Calendar Mode");
    });
}

if (lunarBtn) {
    lunarBtn.addEventListener("click", async () => {
        activeMode = "lunar";
        lunarBtn.style.background = "#2563eb";
        lunarBtn.style.color = "white";
        if (solarBtn) {
            solarBtn.style.background = "#e5e7eb";
            solarBtn.style.color = "black";
        }
        try {
            const res = await fetch("${API_BASE_URL}/api/calendar");
            if (res.ok) {
                const data = await res.json();
                if (lunarDateText) lunarDateText.textContent = data.lunarDate;
            }
        } catch (e) {
            const today = new Date();
            const paksha = today.getDate() <= 15 ? "Shukla Paksha" : "Krishna Paksha";
            if (lunarDateText) {
                lunarDateText.textContent = `Hindu Lunar Date: ${today.getDate()}/${today.getMonth() + 1}/${today.getFullYear()} | ${paksha}`;
            }
        }
        console.log("🌙 Switched to Lunar Calendar Mode");
    });
}

// Timezone Selector
const timezoneSelect = document.getElementById("timezone-select");
const timezoneResult = document.getElementById("timezone-result");

if (timezoneSelect) {
    timezoneSelect.addEventListener("change", async () => {
        const zone = timezoneSelect.value;
        try {
            const response = await fetch(`${API_BASE_URL}/api/timezone?zone=${zone}`);
            if (response.ok) {
                const data = await response.json();
                if (timezoneResult) timezoneResult.textContent = `Time in ${zone}: ${data.time}`;
            }
        } catch (error) {
            const now = new Date();
            if (timezoneResult) timezoneResult.textContent = `Local Time: ${now.toLocaleTimeString()}`;
        }
    });
    // Trigger default selection
    timezoneSelect.dispatchEvent(new Event("change"));
}

// Live Clock Update
function updateLiveClock() {
    const now = new Date();
    const timeString = now.toLocaleTimeString("en-IN", {
        hour: "2-digit",
        minute: "2-digit",
        second: "2-digit"
    });
    const curTimeEl = document.getElementById("current-time");
    if (curTimeEl) {
        curTimeEl.textContent = timeString;
    }
}
setInterval(updateLiveClock, 1000);

// Initialize on page load
updateDateTime();