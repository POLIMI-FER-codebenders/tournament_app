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
    console.log(this.state.edit)
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
  console.log(props.edit)
  if (!props.edit[props.i]) {
    console.log("team")
    return (
      <TeamEntry obj={props.obj} i={props.i} handleChangeMode={event => props.handleChangeMode(event)} />
    )
  } else {
    console.log("details")
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
        <div class="btn" id={props.i} name="leave" onClick={console.log("leaving the team")}>Leave</div>
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
      <TeamPlayersTable />
    </div>
  );
}

function TeamPlayersTable(props) {
  return (

    <table>
      <tr>
        <th>Player</th>
        <th>Role</th>
        <th></th>
        <th></th>
      </tr>
      <tr>
        <td>Sans</td>
        <td>Leader</td>
        <td></td>
        <td></td>
      </tr>
      <tr>
        <td>Hrvoje</td>
        <td>Member</td>
        <td>
          <div class="btn" name="promote" id={props.i}>Promote to leader</div>

        </td>
        <td>
          <div class="btn" name="kick" id={props.i}>Kick from the team</div>

        </td>
      </tr>

    </table>
  );
}


export default ListComponent;