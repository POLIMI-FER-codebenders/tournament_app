import { Component } from "react";
import "../styles/listComponent.css";

class ListComponent extends Component {
  constructor(props) {
    super(props);
    this.state = {
      teams: props.teams,
      edit: []
    };
    this.initStateData()
  }

  initStateData() {
    for (let index = 0; index < this.state.teams.length; index++) {
      this.state.edit[index] = false;
    }
  }

  handleChangeMode(event) {
    this.setState(prevState => {
      const newEdit = prevState.edit;
      newEdit[event.target.id] = !prevState.edit[event.target.id]
      return { edit: newEdit };
    });
  }

  render() {
    return (
      <div class="list">
        <div class="list-headers flex-container">
          <div class="col1 flex-items">Name</div>
          <div class="col2 flex-items">Date created</div>
          <div class="col3 flex-items">Score</div>
          <div class="col4 flex-items"></div>
          <div class="col5 flex-items"></div>
        </div>
        {this.state.teams.map((object, i) => 
          <Entry obj={object} key={i} i={i} edit={this.state.edit} handleChangeMode={event => this.handleChangeMode(event)} />
        )}
      </div>
    );

  }
}

function Entry(props) {
  if (!props.edit[props.i]) {
    return (
      <TeamEntry obj={props.obj} i={props.i} handleChangeMode={event => props.handleChangeMode(event)} />
    )
  } else {
    return (
      <TeamDetailsEntry obj={props.obj} i={props.i} handleChangeMode={event => props.handleChangeMode(event)} />
    )
  }
}

function TeamEntry(props) {
  return (
    <div class="list-entry flex-container">
      <div class="col1 flex-items">{props.obj.name}</div>
      <div class="col2 flex-items">{props.obj.date}</div>
      <div class="col3 flex-items">{props.obj.score}</div>
      <div class="col4 flex-items">
        <div class="btn" id={props.i} name="edit" onClick={props.handleChangeMode}>Edit</div>
      </div>
      <div class="col5 flex-items">
        <div class="btn" id={props.i} name="leave">Leave</div>
      </div>
    </div>
  );
}

function TeamDetailsEntry(props) {
  return (
    <div class="list-entry">
      <div class="flex-container">
        <div class="col1 flex-items">{props.obj.name}</div>
        <div class="col2 flex-items">{props.obj.date}</div>
        <div class="col3 flex-items">{props.obj.score}</div>
        <div class="col4 flex-items">
          <div class="btn" id={props.i} name="confirm" value="false" onClick={props.handleChangeMode}>Confirm</div>
        </div>
        <div class="col5 flex-items">
          <div class="btn" id={props.i} name="cancel" value="false" onClick={props.handleChangeMode}>Cancel</div>
        </div>
      </div>
      <TeamPlayersTable players={props.obj.players} i_team={props.i}/>
    </div>
  );
}

function TeamPlayersTable(props) {
  return (

    <table>
      <thead>
      <tr>
        <th>Player</th>
        <th>Role</th>
        <th></th>
        <th></th>
      </tr>
      </thead>
      <tbody>
        {props.players.map((player, i) => 
          <tr>
            <td>{player.name}</td>
            <td>{player.role}</td>
            <td>
              <div class="btn" name="promote" id={i}>Promote to leader</div>
            </td>
            <td>
              <div class="btn" name="kick" id={i} >Kick from the team</div>

            </td>

          </tr>
        )}
      </tbody>
    </table>
  );
}


export default ListComponent;