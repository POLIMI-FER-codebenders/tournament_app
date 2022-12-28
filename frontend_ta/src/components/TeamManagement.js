import { Component } from "react";
import ListPlayers from "./ListPlayers";
import postData from '../utils';
import { getData } from "../utils";
import { GoToErrorPage } from '../utils';
import "../styles/teamManagement.css";

class ManageTeams extends Component {

  constructor(props) {
    super(props);
    this.state = {
      user: {},
      team: {},
      players: [],
      errorMessage: "",
      badResponse: "",
      display_invite: false,
      isTeamLoaded: false,
      isPlayersLoaded: false,
    };
    this.handleClickInvite = this.handleClickInvite.bind(this);
    this.handleClickPromote = this.handleClickPromote.bind(this);
    this.handleClickLeave = this.handleClickLeave.bind(this);
    this.handleClickKick = this.handleClickKick.bind(this);
  }

  componentDidMount() {
    this.initTeam();
    this.initPlayers();
  }

  initTeam() {
    getData("/api/team/get-mine")
      .then((response) => {
        if (response.status === 200) {
          if (response.result) {
            this.setState({
              isTeamLoaded: true,
              team: response.result
            })
          } else {
            this.setState({
              isTeamLoaded: false,
              errorMessage: "You are not in any team.",
            })
          }
        }
        else {
          this.setState({
            isTeamLoaded: false,
            errorMessage: "the server encountered an error",
            badResponse: response.message
          })
        }
      });

  }

  initPlayers() {
    getData("/api/player/get-all/")
      .then((response) => {
        if (response.status === 200) {
          if (response.result) {
            this.setState({
              isPlayersLoaded: true,
              players: response.result,
              user: response.result.find(p => p.username === sessionStorage.getItem("username"))
            })
          } else {
            this.setState({
              isPlayersLoaded: false,
              errorMessage: "Empty response",
              badResponse: 'No players found in the database'
            })
          }
        }
        else {
          this.setState({
            isPlayersLoaded: false,
            errorMessage: "the server encountered an error",
            badResponse: response.message
          })
        }
      });
  }

  handleClickInvite() {
    const previous_value = this.state.display_invite
    this.setState({ display_invite: !previous_value })
  }

  handleClickLeave() {
    postData("/api/team/leave/")
      .then((response) => {
        if (response.status === 200) {
          alert(`${this.state.user.username}, you left the team ${this.state.team.name}`);
          this.initTeam();
          this.initPlayers();
        }
        else {
          this.setState({
            errorMessage: "the server encountered an error",
            badResponse: response.message
          })
        }
      });
  }

  handleClickKick(event) {
    let data = { idKickedPlayer: event.target.id };
    postData("/api/team/kick-member/", data)
      .then((response) => {
        if (response.status === 200) {
          alert(`The player ${this.state.players.find(p => p.id === parseInt(event.target.id)).username} has been kicked from the team ${this.state.team.name}`);
          this.initTeam();
          this.initPlayers();
        }
        else {
          this.setState({
            errorMessage: "the server encountered an error",
            badResponse: response.message
          })
        };

      });
  }

  handleClickPromote(event) {
    let data = { idPlayer: event.target.id };
    postData("/api/team/members/promote-leader/", data)
      .then((response) => {
        if (response.status === 200) {
          // alert(`The player ${this.state.players.find(p => p.id === parseInt(event.target.id)).username} has been promote to leader for the team ${this.state.team.name}`);
          this.initTeam();
          this.initPlayers();
        }
        else {
          this.setState({
            errorMessage: "the server encountered an error",
            badResponse: response.message
          })
        };

      });
  }

  renderErrorMessage() {
    if (this.state.errorMessage === null) return;
    return (
      <p className='error'>{this.state.errorMessage}</p>
    );
  }


  render() {
    if (this.state.errorMessage === "the server encountered an error") return (<GoToErrorPage path="/error" message={this.state.badResponse} />);
    if (this.state.errorMessage === "Empty response") return (<GoToErrorPage path="/error" message={this.state.badResponse} />);
    return (
      <div className="main-panel">
        <h2>Team Management</h2>
        <div className="flex-container-main">
          {this.displayTeamInfo()}
          {this.invitations()}
        </div>
        {this.renderErrorMessage()}
      </div>
    );
  }

  displayTeamInfo() {
    if (this.state.isTeamLoaded) {
      return (
        <div className="flex-items-main">
          <h3>Your team</h3>
          <div className="flex-container-btn">
            <div className="team-info flex-items-btn">
              <div className="flex-container-info">
                <span className="flex-items-info name-entry">Name:</span>
                <span className="flex-items-info">{this.state.team.name}</span>
              </div>
              <div className="flex-container-info">
                <span className="flex-items-info name-entry">Team policy:</span>
                <span className="flex-items-info">{this.state.team.policy}</span>
              </div>
              <div className="flex-container-info">
                <span className="flex-items-info name-entry">Maximum size:</span>
                <span className="flex-items-info">
                  {this.state.team.maxNumberOfPlayers}
                </span>
              </div>
              <div className="flex-container-info">
                <span className="flex-items-info name-entry">Date of creation:</span>
                <span className="flex-items-info">
                  {this.state.team.dateOfCreation}
                </span>
              </div>
            </div>
            {this.actionInvite()}
          </div>
          {this.displayTeamMember()}
        </div>
      )
    }
  }

  displayTeamMember() {
     if (this.state.isTeamLoaded && this.state.isPlayersLoaded) {
      return (
        <table>
          <thead>
            <tr>
              <th>Player</th>
              <th>Role</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            <tr></tr>
            {this.state.team.teamMembers.map((player, i) =>
              <tr key={i} id={player.id}>
                <td>{player.username}</td>
                <td>{player.role}</td>
                <td>
                  {this.actionPlayer(player.id)}
                </td>
              </tr>
            )}
          </tbody>
        </table>
      )
    }
  }

  actionPlayer(idPlayer) {
    if (this.state.isPlayersLoaded) {
      if (idPlayer === this.state.user.id && this.state.user.role !== "LEADER") {
        return (<div class="btn btn-red" name="leave" id={idPlayer} onClick={this.handleClickLeave} >Leave</div>);
      } else if (idPlayer !== this.state.user.id && this.state.user.role === "LEADER") {
        return (
          <div className="btn-container">
            <div class="btn btn-red" name="kick" id={idPlayer} onClick={this.handleClickKick}>Kick from the team</div>
            <div class="btn btn-blue" name="promote" id={idPlayer} onClick={this.handleClickPromote}>Promote to leader</div>
          </div>
        );
      } else if (this.state.team.teamMembers.length === 1) {
        return (<div class="btn btn-red" name="delete" id={idPlayer} onClick={this.handleClickLeave} >Delete</div>);
      } else return;
    }
  }

  actionInvite() {
    if (this.state.isPlayersLoaded) {
      if (this.state.user.role === "LEADER" && this.state.team.teamMembers.length < this.state.team.maxNumberOfPlayers) {
        return (
          <div class="btn btn-yellow flex-items-btn" name="display_invite" onClick={this.handleClickInvite}>Invite players</div>
        );
      } else return;
    }
  }

  invitations() {
    if (this.state.display_invite && this.state.isPlayersLoaded && this.state.user.role === 'LEADER') {
      return (
        <div className="flex-items-main">
          <h3>Send invitation to join the team</h3>
          <ListPlayers
            players={this.state.players
              .filter(user => !this.state.team.teamMembers.find(p => p.id === user.id))
            }
            teamId={this.state.team.id}
          />
        </div>
      )
    }
    else return
  }
}

export default ManageTeams;