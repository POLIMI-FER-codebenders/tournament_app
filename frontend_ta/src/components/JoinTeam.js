import { Component, useState } from "react";
import "../styles/JoinTeam.css"
import postData, { getData } from "../utils";
import React from 'react';

function TeamEntry(props) {
  const [open, setOpen] = useState(false);
  const toggle = () => {setOpen(!open);};
  
  return (
    <div class="team-entry">
      <button class="item button-container"
       onClick={toggle}>{props.team.name}
      </button>
      
      {open && (
        <div class="item container">
          <p>Date of creation: {props.team.date}</p>
          <p>Members:
            {props.team.members.map((m) => 
              <div class="item-member">
                {m} 
              </div>)} 
          </p>
          <p>The team is {props.team.type} to new players 
            {props.team.type === "open" && (
              <button class="item button-container btn-join"
                onClick={()=> postData("api/team/join", props.team.id)
                              .then((response) => {
                                if (response.status === 200) {
                                  alert(`You have joined team ${props.team.name}`);
                                  //this.initTeam();
                                }
                                else {
                                  JoinTeam.setState({
                                    errorMessage: "the server encountered an error",
                                    badResponse: response.message});
                                }})}>
                Join this team
              </button>
            )}
          </p>
        </div>
      )}
    </div>
  );
};

function InvitationEntry(props){
  return(
    <div class="invitation-entry">
      <p class="inv-team">Invitation from {props.invitation.team}</p> 
      <button class="item button-container btn-accept"
       onClick={()=> postData("api/invitation/accept", props.invitation.id)
                              .then((response) => {
                                if (response.status === 200) {
                                  alert(`You have joined team ${props.invitation.team}`);
                                  //this.initTeam();
                                }
                                else {
                                  JoinTeam.setState({
                                    errorMessage: "the server encountered an error",
                                    badResponse: response.message});
                                }})}>
        Accept
      </button>
      <button class="item button-container btn-decline"
        onClick={()=> postData("api/invitation/decline", props.invitation.id)
                              .then((response) => {
                                if (response.status === 200) {
                                  alert(`You have rejected invitation to team ${props.invitation.team}`);
                                  //this.initTeam();
                                }
                                else {
                                  JoinTeam.setState({
                                    errorMessage: "the server encountered an error",
                                    badResponse: response.message});
                                }})}>
        Decline
      </button>
    </div>
  );
}

class JoinTeam extends Component{
  constructor(props) {
    super(props);
    this.state = {
      user: {},
      teams: {},
      invitations: [{"team":"Tithonus"}, {"team":"GoldenBoys"}],
      data : [{"name":"team1", "type":"open", "members":["Amy", "Ric"], "size":2},{"name":"team2", "type":"closed", "members":["Lenny", "Benny", "Fanny"], "size":3}],
      view_inv: "Check"
    };
  }

  componentDidMount() {
    getData("/api/team/get-all").then((response)=> {
      if (response.status === 200) 
        this.setState({data: response.result});
      else 
        console.log("error");
    });

    // TODO API: get all invitations for given player's id (all info of the invitation)
    // e: [{"id":1, "team":"TeamD", "date":<datereceived>}, {"id":2, "Team":"CoolTeam", ...}] // list of invitation objects
    getData("/api/invitations/get-player-id").then((response)=> {
      if (response.status === 200) 
        this.setState({data: response.result});
      else 
        console.log("error");
    });
  }

  render(){
      return (
        <div class="main-panel">
          <button class="item button-container"
            onClick={() => {let b = this.state.view_inv;
                            b === "Check" ? this.setState({view_inv : "Close"}) : this.setState({view_inv : "Check"})}}
                            >{this.state.view_inv} Invitations ({this.state.invitations.length})
          </button>
          {this.state.view_inv === "Close" &&
          <div>
          {this.state.invitations.map((object, i) => <InvitationEntry invitation={object} key={i} />)}
          </div>}

          <h2>Teams</h2>
          {this.state.data.map((object, i) => <TeamEntry team={object} key={i} />)}
        </div>
    );
  }
}

export default JoinTeam;