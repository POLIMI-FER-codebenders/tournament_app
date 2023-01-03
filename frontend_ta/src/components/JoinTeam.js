import { Component, useState } from "react";
import "../styles/JoinTeam.css"
import postData, { getData, GoToErrorPage } from "../utils";
import React from 'react';

function InitInTeam(){
  const [state, dispatch] = useGlobalState();
  getData("/api/team/get-mine")
  .then((response) => {
    if (response.status === 200) {
      if (!response.result)
        dispatch({ alreadyInTeam : "" });
      else
        dispatch({ alreadyInTeam : response.result.name });
    }
  });
  return <div></div>;
}

const initialGlobalState = {
  alreadyInTeam : ""
};
const GlobalStateContext = React.createContext(initialGlobalState);
const DispatchStateContext = React.createContext(undefined);

// Global State provider & hooks
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
  const [joined,setjoined]=useState(false);
  let teambuttonclassname;
  if(!props.team.full)
  teambuttonclassname = "teambutton " + "greenteam";
  else teambuttonclassname = "teambutton " + "redteam";
  let buttonname;
  
  if(state.alreadyInTeam === props.team.name) 
  buttonname='you are already inside this team'; 
  else buttonname=  'Join Team';
  
  return (
    <div className="team-entry">
      <button className={teambuttonclassname}
       onClick={() => setOpen(!open)}>{props.team.name}
      </button>
      
      {open && (
        <div className="teamdescription">
          <p className="pteamdesc1">Date of creation: 
            <span className="flex-items-info">
              {props.team.dateOfCreation[2]}.{props.team.dateOfCreation[1]}.{props.team.dateOfCreation[0]}.
            </span>
          </p>
          <p  className="pteamdesc2">Members ({props.team.members.length}) of maximum size ({props.team.maxNumberOfPlayers}) :
            {props.team.members.map((m) => 
              <div className="item-member">
                <span>{m.username}</span> {m.role === "LEADER" && (<span> (leader)</span>)}
              </div>)} 
          </p>
          <p className="pteamdesc3">The team is {props.team.policy} to new players. <br/>
            {props.team.members.length === props.team.maxNumberOfPlayers && (<span>Can't join, currently full.</span>)}
            {props.team.policy === "OPEN" && props.team.members.length < props.team.maxNumberOfPlayers && !joined && (
              <button className={state.alreadyInTeam === props.team.name ? 'btn-inactive' : 'btn-active'}
                onClick={()=> { 
                                
                                postData("/api/team/join", {"idTeam" : props.team.id})
                                .then((response) => {
                                  if (response.status === 200) {
                                    setjoined(true);
                                    dispatch({ alreadyInTeam : props.team.name });
                                  }
                                  else {
                                    alert(response.message);
                                  }
                                })
                              }}>{buttonname}
                
              </button>
              
            )}
            {joined && <p>successfully joined the team</p>}
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
    <div className="invitation-entry">
      <p className="inv-team">Invitation from {props.invitation.team.name}</p> 

      {!hide && ( <div className="buttoninvitationcontainer">
        <button className={(!state.alreadyInTeam ? 'btn-active invitationbutton' : 'btn-inactive invitationbutton')} 
          onClick={()=> postData("/api/invitation/accept", {"idInvitation" : props.invitation.id})
                              .then((response) => { 
                                if (response.status === 200) {
                                  dispatch({ alreadyInTeam : props.invitation.team.name });
                                  setHide(true);
                                  setStatus("accepted");
                                }
                                else {
                                  alert(response.message);
                                }})}>
          Accept
        </button>
        <button className="btn-decline invitationbutton"
          onClick={()=> postData("/api/invitation/reject", {"idInvitation" : props.invitation.id})
                          .then((response) => {
                            if (response.status === 200) {
                              setHide(true);
                              setStatus("rejected");
                            } 
                            else {
                              alert(response.message);
                            }})}>
          Decline
        </button>
      </div>)}
      {hide && (<p id="acceptedinvitation"> : {status} </p>)}
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
      alreadyInTeam: "",
      initTeam: true,
      errorMessage: "",
      badResponse: "",
      backupteams:"",
      teamname:""
    };
    this.handleChange= this.handleChange.bind(this);
  }
  handleChange(event) {
    let teamname = event.target.value;
    this.setState({teamname: event.target.value , data: this.state.backupteams.filter(entry => entry.name.startsWith(teamname))});
  }
  
  componentDidMount() {
    getData("/api/team/get-all").then((response)=> {
      if (response.status === 200) 
        this.setState({data: response.result,backupteams:response.result});
      else {
        this.setState({
          errorMessage: "the server encountered an error",
          badResponse: response.message
        });
      }
    });
    
    getData("/api/invitation/pending").then((response)=> {
      if (response.status === 200) 
        this.setState({invitations: response.result});
      else {
        this.setState({
          errorMessage: "the server encountered an error",
          badResponse: response.message
        });
      }
    });
  }

  render(){
    if (this.state.errorMessage == "the server encountered an error") return (<GoToErrorPage path="/error" message={this.state.badResponse} />);
    return (
      <GlobalStateProvider>
      {this.state.initTeam && (<div><InitInTeam />{this.setState({initTeam : false})}</div>)}
      <div className="main-panel">
        <button id="checkinvbutton" className="item button-container "
          onClick={() => { 
                          getData("/api/invitation/pending").then((response)=> {
                            if (response.status === 200) 
                              this.setState({invitations: response.result});
                            else {
                              this.setState({
                                errorMessage: "the server encountered an error",
                                badResponse: response.message});
                            }
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
          {this.state.invitations.map((object, i) => <InvitationEntry invitation={object} key={i}/>)}
        </div>}

        <h2>Teams</h2>
        <div id="searchbartour">
          <label className="labelsearchtour" htmlFor='toursearchtextarea' >
        Search team by name
        <textarea className="textareasearchtour" id="toursearchtextarea" value={this.state.teamname} onChange={this.handleChange} />
      </label>
  </div>
        {this.state.data.map((object, i) => <TeamEntry team={object} key={i} />)}
      </div>
      </GlobalStateProvider>
    );
  }
}

export default JoinTeam;