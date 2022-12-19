import { Component } from "react";
// import "../styles/listPlayers.css";
import "../styles/teamManagement.css";
import postData from '../utils';
import { getData } from "../utils";
import { GoToErrorPage } from '../utils';

class ListPlayers extends Component {
  constructor(props) {
    super(props);
    this.state = {
      players: props.players,
      errorMessage: "",
      badResponse: "",
      teamId: props.teamId,
      invitNotSend: []
    };
  }

  componentDidMount() {
    let invitNotSend = [];
    this.state.players.forEach(p => {
      invitNotSend.push(p.id);
    });
    let data = {
      idTeam: this.state.teamId
    };
    getData("/api/invitation/get-from-team", data)
    .then((response) => {
      if (response.status === 200) {
        if (response.result) {
          response.result.forEach(invite => {
            invitNotSend = invitNotSend.filter(i => i !== invite.ID_invited_player)
          });
          this.setState({
            invitNotSend: invitNotSend
          })
        }
      }
      else {
        this.setState({
          errorMessage: "the server encountered an error",
          badResponse: response.message
        })
      }
    });
  }

  handleSendInvite(event) {
    let url_invit = "/api/invitation/create/";
    let data = {
      idInvitedPlayer: event.target.id,
      idTeam: this.state.teamId
    };
    postData(url_invit, data)
      .then((response) => {
        if (response.status === 200) {
          alert(`An invitation has been send to the player ${event.target.id} to join the team ${this.state.teamId}`);
          let tmpInvitNotSend = this.state.invitNotSend;
          this.setState({
            invitNotSend: tmpInvitNotSend.filter(elem => elem !== parseInt(event.target.id))
          })
        }
        else {
          this.setState({
            errorMessage: "the server encountered an error",
            badResponse: response.message
          })
        }
      });
  }

  render() {
    if (this.state.errorMessage == "the server encountered an error") return (<GoToErrorPage path="/error" message={this.state.badResponse} />);
    return (
      <div class="list">
        <div class="list-headers flex-container-list">
          <div class="col1 flex-items-list">Name</div>
          <div class="col2 flex-items-list">Score</div>
          <div class="col3 flex-items-list"></div>
        </div>
        {this.state.players.map((object, i) =>
          <PlayerEntry
            obj={object}
            key={i}
            handleClick={event => this.handleSendInvite(event)}
            teamId={this.state.teamId}
            isInactive={this.state.invitNotSend.find(i => i === object.id) === undefined}
          />
        )}
      </div>
    );

  }
}

function PlayerEntry(props) {
  if (props.isInactive) {

    return (
      <div class="list-entry flex-container-list">
        <div class="col1 flex-items-list">{props.obj.username}</div>
        <div class="col2 flex-items-list">{props.obj.score}</div>
        <div class="col3 flex-items-list">
          <div class="btn-inactive" id={props.obj.id}>Send invitation</div>
        </div>
      </div>
    );

  } else {

    return (
      <div class="list-entry flex-container-list">
        <div class="col1 flex-items-list">{props.obj.username}</div>
        <div class="col2 flex-items-list">{props.obj.score}</div>
        <div class="col3 flex-items-list">
          <div class="btn btn-green" id={props.obj.id} onClick={props.handleClick}>Send invitation</div>
        </div>
      </div>
    );
  }

}


export default ListPlayers;