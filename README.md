# 🔥 Toaster Oven Simulator

This project is a **Java-based simulation of a toaster oven**, built as part of a Software Engineering course. It demonstrates object-oriented design, JavaFX-based user interfaces, and multi-threaded device simulation.

The simulation models real-world device behavior and interaction, including visual indicators and toast cycle logic.

---

## 🎯 Features

- 🍞 Simulates toaster oven components (heating elements, timer, buttons)
- 🖥️ JavaFX-based GUI to interact with the device
- ⚙️ Functional buttons for Start, Stop, Modes, and Presets
- ⏱️ Timer and cooking cycle tracking
- 🔄 Internal state machine to manage heating and idle states

---

## 🛠️ Technologies Used

- **Java 17+**
- **JavaFX** for GUI interface
- **Multi-threading** for simulating device behavior

---

## 🚀 How to Run

1. **Run `FXDeviceSimulator`**  
   This launches the graphical interface that simulates the toaster oven's external controls and screen. It must be started **first** so it’s ready to receive state updates.

2. **Run `Main.java`**  
   This starts the simulation backend, initializes the toaster oven logic, and connects to the GUI.

Once both components are running, you'll be able to:
- Select presets (Pizza/Nuggets)
- Select time and heat levels
- Select modes (Bake/Broil/Roast)
- Start and stop cooking
- Watch a full cycle complete with heating, and countdown

---

### ⚠️ JavaFX Dependency

> **Note:** This project uses JavaFX, which may not be bundled with your JDK.


## 🎓 Educational Value

This project reinforces:
- Modular software design
- Event-driven programming
- Simulating real hardware with GUI and backend logic
- Thread safety and device coordination

---
