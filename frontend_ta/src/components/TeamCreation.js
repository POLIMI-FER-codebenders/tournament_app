import { Component } from "react";
import postData from "../utils";
class TeamCreation extends Component {
  constructor(props) {
    super(props);
    this.state = {
      name: "",
      type: "OPEN"
    };
    this.handleChange = this.handleChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);  
  }

  handleChange(event) {
    this.setState({type: event.target.value});
  };

  handleSubmit(event) {
    event.preventDefault();
    console.log(this.state.type);
    let data={name:this.state.name,maxNumberOfPlayers:4,policy:this.state.type}
      postData("/api/team/create",data);
    alert(`The name you entered was: ${this.state.name}, ${this.state.type} to new members`);
  };


  render() {
    return (
      <div class="main-panel">
        <h2>Team creation</h2>

        <form onSubmit={this.handleSubmit}>
          <div class="container">
            <div class="input-container">
              <label>
                Enter new team name:
                <input
                  type="text"
                  value={this.state.name}
                  onChange={(e) => this.setState({ name: e.target.value })}
                />
              </label>
            </div>
            <div class="container">
              <p>Please select whether your team will be open or closed to new members:</p>
              <select class="selector" value={this.state.type} onChange={this.handleChange}>
                <option value="OPEN">OPEN</option>
                <option value="CLOSED">CLOSED</option>
              </select>
            </div>
            <input type="submit" class="item" />
          </div>
        </form>
      </div>
    );
  }
}

export default TeamCreation