import { Component, useState } from "react";

export function CreateTournament() {
    const [name, setName] = useState("");
    const [type, setType] = useState("Single Elimination");
    const [size, setSize] = useState(2);
    const [start_date, setDate] = useState("");
  
    const handleChange = (event) => {
      if(event.target.type === "number")
        setSize(event.target.value);
      else if (event.target.type === "date")
        setDate(event.target.value);
      else
        setType(event.target.value);
    };
  
    const handleSubmit = (event) => {
      event.preventDefault();
      alert(`you want to create tournament: ${name}, of type ${type}, for ${size} teams, starting on ${start_date}`);
    };
  
    return (
      <div class="main-panel">
        <h2>Tournament creation</h2>
        
        <form onSubmit={handleSubmit}>
        <div class="container">
          <div class="input-container">
            <label>
              Enter new tournament name:
              <input
                type="text"
                value={name}
                onChange={(e) => setName(e.target.value)}
              />
            </label>
          </div>
          <div class="container">
            <p>Please select whether the tournament will be Single Elimination or League:</p>
            <select class="selector" value={type} onChange={handleChange}>
              <option value="Single Elimination">Single Elimination</option>
              <option value="League">League</option>
            </select>
            <label>Enter the number of participants (2-16):
                <input 
                    type="number" name="size" 
                    value={size} 
                    onChange={handleChange}
                />
            </label>
            <label>Select the starting date
              <input  name="starting_date"  type="date" 
                value={start_date} 
                className="form-control"  
                onChange={handleChange} 
              />
            </label>
          </div>
          <input type="submit" class="item"/>
          </div>
        </form>
      </div>
    );
}