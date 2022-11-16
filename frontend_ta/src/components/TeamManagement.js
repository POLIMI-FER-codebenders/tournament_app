import { Component, useState } from "react";

export function CreateTeam() {
    const [name, setName] = useState("");
    const [type, setType] = useState("open");
  
    const handleChange = (event) => {
      setType(event.target.value);
    };
  
    const handleSubmit = (event) => {
      event.preventDefault();
      alert(`The name you entered was: ${name}, ${type} to new members`);
    };
  
    return (
      <div class="main-panel">
        <h2>Team creation</h2>
        
        <form onSubmit={handleSubmit}>
        <div class="container">
          <div class="input-container">
            <label>
              Enter new team name:
              <input
                type="text"
                value={name}
                onChange={(e) => setName(e.target.value)}
              />
            </label>
          </div>
          <div class="container">
            <p>Please select whether your team will be open or closed to new members:</p>
            <select class="selector" value={type} onChange={handleChange}>
              <option value="Open">Open</option>
              <option value="Closed">Closed</option>
            </select>
          </div>
          <input type="submit" class="item"/>
          </div>
        </form>
      </div>
    );
}

function ManageTeam(props){
  return(
    <p>Manage team {props.name}</p>
  );
}

function TeamEntry(props){
  let flag = false;
  return (
    <div class="entry">
      <button class="item" 
        onClick={()=>{flag = true}}>
        {props.obj.name}
      </button>
    </div>
  );
}

export function ListTeams() {
  const data =[{"name":"team1"},{"name":"team2"}];
  const rows = [];
  for(let i = 0; i < data.size; i++) {
    rows.push(TeamEntry(data[i].name));
  }
  return (
      <div class="main-panel">
        <h2>Team management</h2>
        <p>Select the team to manage</p>
        {data.map((object, i) => <TeamEntry obj={object} key={i} />)}
      </div>
  );
}

class ManageTeams extends Component{
  render (){
    return(
      <ListTeams />
  );}
}

export default ManageTeams;