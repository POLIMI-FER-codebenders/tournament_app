import { Component, useState } from "react";
import "../styles/JoinTeam.css"
import postData, { getData } from "../utils";
import React from 'react';

function TeamEntry(props) {
  const [open, setOpen] = useState(false);
  const toggle = () => {setOpen(!open);};
  const [active, setActive] = useState(true);
  const toggle_active = () => {setActive(false);};
  
  return (
    <div class="team-entry">
      <button class="item button-container"
       onClick={toggle}>{props.team.name}
      </button>
      
      {open && (
        <div class="item container">
          <p>Date of creation: 
            <span className="flex-items-info">
              {props.team.dateOfCreation[2]}.{props.team.dateOfCreation[1]}.{props.team.dateOfCreation[0]}.
            </span>
          </p>
          <p>Members ({props.team.members.length}) of maximum size ({props.team.maxNumberOfPlayers}) :
            {props.team.members.map((m) => 
              <div class="item-member">
                <span>{m.username}</span> {m.role === "LEADER" && (<span> (leader)</span>)}
              </div>)} 
          </p>
          <p>The team is {props.team.policy} to new players. <br/>
            {props.team.members.length === props.team.maxNumberOfPlayers && (<span>Can't join, currently full.</span>)}
            {props.team.policy === "OPEN" && props.team.members.length < props.team.maxNumberOfPlayers && (
              <button className={active ? 'btn-active' : 'btn-inactive'}
                onClick={()=> postData("/api/team/join", {"idTeam" : props.team.id})
                                .then((response) => {
                                  if (response.status === 200) {
                                    alert(`You have joined team ${props.team.name}`);
                                    toggle_active;
                                    //window.location.reload();
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
}

function InvitationEntry(props){
  return(
    <div class="invitation-entry">
      <p class="inv-team">Invitation from {props.invitation.team.name}</p> 
      <button className="item button-container btn-accept" 
       onClick={()=> postData("/api/invitation/accept", {"idInvitation" : props.invitation.id})
                              .then((response) => { 
                                if (response.status === 200) {
                                  alert(`You have joined team ${props.invitation.team.name}`);
                                  //TODO refresh
                                }
                                else {
                                  JoinTeam.setState({
                                    errorMessage: "the server encountered an error",
                                    badResponse: response.message});
                                }})}>
        Accept
      </button>
      <button class="item button-container btn-decline"
        onClick={()=> postData("/api/invitation/reject", {"idInvitation" : props.invitation.id})
                          .then((response) => {
                            if (response.status === 200) {
                              alert(`You have rejected invitation to team ${props.invitation.team.name}`);
                              //TODO: refresh
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
      invitations: [],
      data : [],
      view_inv: "Check",
      alreadyInTeam: true
    };
  }

  componentDidMount() {
    getData("/api/team/get-all").then((response)=> {
      if (response.status === 200) 
        this.setState({data: response.result});
      else 
        console.log("error");
    });

    getData("/api/team/get-mine")
      .then((response) => {
        if (response.status === 200) {
          if (response.result) 
            this.setState({ alreadyInTeam: true })
          else
            this.setState({ alreadyInTeam: false })
        }
        else {
          this.setState({
            isTeamLoaded: false,
            errorMessage: "the server encountered an error",
            badResponse: response.message
          })
        }
      });

    getData("/api/invitation/pending").then((response)=> {
      if (response.status === 200) 
        this.setState({invitations: response.result});
      else 
        console.log("error");
    });
  }

  render(){
    return (
      <div class="main-panel">
        <button class="item button-container"
          onClick={() => { let b = this.state.view_inv;
                            b === "Check" ? this.setState({view_inv : "Close"}) : this.setState({view_inv : "Check"})
                  }}>
          {this.state.view_inv} Invitations ({this.state.invitations.length})
        </button>
        {this.state.view_inv === "Close" &&
        <div>
          {this.state.invitations.map((object, i) => <InvitationEntry invitation={object} key={i} active={!this.state.alreadyInTeam}/>)}
        </div>}

        <h2>Teams</h2>
        {this.state.data.map((object, i) => <TeamEntry team={object} key={i} />)}
      </div>
    );
  }
}

export default JoinTeam;