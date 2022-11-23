import { Component, useState } from "react";
import ListComponent from "./ListTeamComponent";

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


class ManageTeams extends Component{
  render (){
    const data =[
      {"name":"Steaming hot coffee enjoyers", "date":"16.10.2022", "score":"0",
        "players": [
          {"name":"Fanny", "role": "Leader"},
          {"name":"ric", "role": "Member"},
          {"name":"Vrganj", "role": "Member"},
          {"name":"Simon99", "role": "Member"}
        ]
      },
      {"name":"Guys from the basement", "date":"14.10.2022", "score":"32", 
        "players": [
          {"name":"Sans", "role": "Leader"},
          {"name":"Hrvoje", "role": "Member"},
          {"name":"Bob", "role": "Member"},
          {"name":"SanAndreas", "role": "Member"}
        ]
      }
    ];  
    return(
      <div class="main-panel">
        <h2>Team Management</h2>
        <ListComponent teams={data}/>
      </div>
  );}
}

export default ManageTeams;