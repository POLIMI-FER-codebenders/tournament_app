import { Component, useState } from "react";
import "../styles/JoinTeam.css"
import { getData } from "../utils";
import React, {  useEffect } from 'react';

function Member(props){
  return(<div class="item-member">{props.name}</div>);
}

function TeamEntry(props) {
  const [open, setOpen] = useState(false);
  const toggle = () => {
    setOpen(!open);
  };
  /* const members = [];
  for(let i = 0; i < props.team.size; i++) {
    members.push(props.team.type);
  } */
  
  return (
    <div class="team-entry">
      <button class="item button-container"
       >{props.team.name}
      </button>
      
      {open && (
        <div class="item container">
        <p>Members</p>
        {props.team.members.map((m) => <Member name={m} />)} 
        {props.team.type === "open" && (
          <button class="item button-container"
            onClick={toggle}>Join this team
          </button>
        )}
        {props.team.type !== "open" && (
          <button class="item button-container btn-join"
            onClick={toggle}>Join this team
          </button>
        )}
        </div>
        
      )}

      
    </div>
  );
};

export class  ListTeams extends React.Component {
  
  constructor(props) {
    super(props);
    this.state = {
      data:[]
    };

  }
  componentDidMount() {
    
    getData("/api/team/get-all").then((response)=> {
     if (response.status == 200) {
      this.setState({data: response.result});
     }
     else console.log("error");
    }
    );
  }
   
   
    //const data =[{"name":"team1", "type":"open", "members":["Anny", "Ric"], "size":2},{"name":"team2"}];
    
    render(){
      return (
        <div class="main-panel">
          <h2>Teams</h2>
          {this.state.data.map((object, i) => <TeamEntry team={object} key={i} />)}
          
        </div>
    );
  }
}

class JoinTeam extends Component{
  render (){
    return(
      <ListTeams />
  );}
}

export default JoinTeam;