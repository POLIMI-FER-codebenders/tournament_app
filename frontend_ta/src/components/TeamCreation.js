import { Component } from "react";
import postData from "../utils";
import '../styles/createTeam.css';
class TeamCreation extends Component {
  constructor(props) {
    super(props);
    this.state = {
      name: "",
      type: "OPEN",
      messageError:null
    };
    this.handleChange = this.handleChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);  
    this.renderErrorMessage=this.renderErrorMessage.bind(this);
  }

  renderErrorMessage() {
    if (this.state.messageError === null) return;
    return (
      <p >{this.state.messageError}</p>
    );
  }

  handleChange(event) {
    this.setState({type: event.target.value});
  };

  handleSubmit(event) {
    event.preventDefault();
    let maxnumberofplayers=document.getElementById("cteamsize-selector").value;
    if(this.state.name.length>255) this.setState({messageError:"the name must be 255 char maximum"});
    else if(maxnumberofplayers>10 || maxnumberofplayers<1)this.setState({messageError:"team size must be from 1 to 127"})
    let data={name:this.state.name,maxNumberOfPlayers:maxnumberofplayers,policy:this.state.type}
      postData("/api/team/create", data).then((response)=> {
        if (response.status === 200) {
         this.setState({messageError: "team successfully created"});    
        }
        else {
          this.setState({messageError:response.message});
       }
      }
       );
  };


  render() {
    return (
      <div id="createteam-maindiv">
        <h2>Team creation</h2>

        <form onSubmit={this.handleSubmit}>
          
            <div class="input-createteam-container">
              <label htmlFor="createteamnameinput" className="createTeamLabel">
                Enter new team name:
                <input className="inputcreateteam"
                  type="text" id="createteamnameinput"
                  value={this.state.name}
                  onChange={(e) => this.setState({ name: e.target.value })}
                />
              </label>
            </div>
            <div class="input-createteam-container">
            <label className="createTeamLabel" htmlFor="cteamsize-selector">Enter the size of teams:
              <input
                type="number" className="inputcreateteam" name="size" id="cteamsize-selector" required min="1" max="10"
                />
            </label>
            </div>
            <div class="input-createteam-container">
              <label className="createTeamLabel" htmlFor="createteamselector">Please select whether your team will be open or closed to new members:</label>
              <select  id="createteamselector" value={this.state.type} onChange={this.handleChange}>
                <option value="OPEN">OPEN</option>
                <option value="CLOSED">CLOSED</option>
              </select>
            </div>
            <div id="button-createteam-container">
            <input type="submit" id="createteambutton"  value="Create team"/>
            </div>
        </form>
        {this.renderErrorMessage()}
      </div>
    );
  }
}

export default TeamCreation