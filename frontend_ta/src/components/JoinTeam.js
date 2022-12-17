import { Component, useState } from "react";
import "../styles/JoinTeam.css"
import postData, { getData } from "../utils";
import React from 'react';

function InitInTeam(){
  const [state, dispatch] = useGlobalState();
  getData("/api/team/get-mine")
  .then((response) => {
    if (response.status === 200) {
      if (!response.result) 
      dispatch({ alreadyInTeam : false });
      return <div></div>;
    }
  });
  dispatch({ alreadyInTeam : true });
  return <div></div>;
}

const initialGlobalState = {
  alreadyInTeam : true
};
const GlobalStateContext = React.createContext(initialGlobalState);
const DispatchStateContext = React.createContext(undefined);

/**
 * Global State provider & hooks
 */
const GlobalStateProvider = ({ children }) => {
  const [state, dispatch] = React.useReducer(
    (state, newValue) => ({ ...state, ...newValue }),
    initialGlobalState
  );
  return (
    <GlobalStateContext.Provider value={state}>
      <DispatchStateContext.Provider value={dispatch}>
        {children}
      </DispatchStateContext.Provider>
    </GlobalStateContext.Provider>
  );
};

export const useGlobalState = () => [
  React.useContext(GlobalStateContext),
  React.useContext(DispatchStateContext)
];

function TeamEntry(props) {
  const [open, setOpen] = useState(false);
  const [state, dispatch] = useGlobalState();
  
  return (
    <div class="team-entry">
      <button class="item button-container"
       onClick={() => setOpen(!open)}>{props.team.name}
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
              <button className={!state.alreadyInTeam ? 'btn-active' : 'btn-inactive'}
                onClick={()=> postData("/api/team/join", {"idTeam" : props.team.id})
                                .then((response) => {
                                  if (response.status === 200) {
                                    alert(`You have joined team ${props.team.name}`);
                                    dispatch({ alreadyInTeam : true });
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
  const [hide, setHide] = useState(false);
  const [status, setStatus] = useState("");
  const [state, dispatch] = useGlobalState();  

  return(
    <div class="invitation-entry">
      <p class="inv-team">Invitation from {props.invitation.team.name}</p> 
      {!hide && ( <div>
      <button className={'item button-container ' + (!state.alreadyInTeam ? 'btn-active' : 'btn-inactive')} 
       onClick={()=> postData("/api/invitation/accept", {"idInvitation" : props.invitation.id})
                              .then((response) => { 
                                if (response.status === 200) {
                                  dispatch({ alreadyInTeam : true });
                                  setHide(true);
                                  setStatus("accepted");
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
                              setHide(true);
                              setStatus("rejected");
                            } 
                            else {
                              JoinTeam.setState({
                              errorMessage: "the server encountered an error",
                              badResponse: response.message});
                            }})}>
        Decline
      </button>
      </div>)}
      {hide && (<span>You have {status} this invitation</span>)}
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
      alreadyInTeam: true,
      initTeam: true
    };
  }
  
  componentDidMount() {
    getData("/api/team/get-all").then((response)=> {
      if (response.status === 200) 
        this.setState({data: response.result});
      else 
        console.log("error");
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
      <GlobalStateProvider>
      {this.state.initTeam && (<div><InitInTeam />{this.setState({initTeam : false})}</div>)}
      <div class="main-panel">
        <button class="item button-container"
          onClick={() => { 
                          getData("/api/invitation/pending").then((response)=> {
                            if (response.status === 200) 
                              this.setState({invitations: response.result});
                            else 
                              console.log("error");
                          });
                          if (this.state.view_inv === "Check") {
                            this.setState({view_inv : "Close"});
                          } else {
                            this.setState({view_inv : "Check"});
                          }
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
      </GlobalStateProvider>
    );
  }
}

export default JoinTeam;