import { Component } from "react";
import postData from "../utils";
import '../styles/App.css';
import '../styles/forms.css';
class TeamCreation extends Component {
  constructor(props) {
    super(props);
    this.state = {
      name: "",
      type: "OPEN",
      messageError: null,
      messageSuccess: null,
    };
    this.handleChange = this.handleChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
    this.renderErrorMessage = this.renderErrorMessage.bind(this);
  }

  renderErrorMessage() {
    if (this.state.messageError === null && this.state.messageSuccess !== null) {
      return ( <p className='success'>{this.state.messageSuccess}</p>);
    }
    else if (this.state.messageError !== null && this.state.messageSuccess === null) {
      return (<p className='error'>{this.state.messageError}</p>); 
    } else return
  }

  handleChange(event) {
    this.setState({ type: event.target.value });
  };

  handleSubmit(event) {
    event.preventDefault();
    let maxnumberofplayers = document.getElementById("sizeteam").value;
    if (this.state.name.length > 255) this.setState({ messageError: "the name must be 255 char maximum", messageSuccess: null  });
    else if (maxnumberofplayers > 10 || maxnumberofplayers < 1) this.setState({ messageError: "team size must be from 1 to 127", messageSuccess: null  })
    let data = { name: this.state.name, maxNumberOfPlayers: maxnumberofplayers, policy: this.state.type }
    postData("/api/team/create", data).then((response) => {
      if (response.status === 200) {
        this.setState({ messageSuccess: "team successfully created", messageError: null });
      }
      else {
        this.setState({ messageError: response.message, messageSuccess: null });
      }
    }
    );
  };

  displayDescPolicy() {
    if (this.state.type === "OPEN") {
      return (
        <div className="descInput" id="expPolicy">
          Players can join the team themselves.
        </div>

      )
    } else if (this.state.type === "CLOSED") {
      return (
        <div className="descInput" id="expPolicy">
          Players must be invited to join the team.
        </div>

      )
    }
  }


  render() {
    return (
      <div className="main-panel">
        <h2>Team creation</h2>

        <form onSubmit={this.handleSubmit}>

          <div className="input-container">
            <label htmlFor="nameteam">Name</label>
            <input
              type="text" id="nameteam"
              value={this.state.name}
              onChange={(e) => this.setState({ name: e.target.value })}
            />
          </div>
          <div className="input-container">
            <label htmlFor="sizeteam">Size</label>
            <input
              type="number" name="size" id="sizeteam" required min="1" max="10"
              placeholder="Number of team members"
            />
          </div>
          <div className="input-container">
            <label htmlFor="policyteam">Policy
            </label>
            <select className="selector" id="policyteam" value={this.state.type} onChange={this.handleChange} aria-describedby="expPolicy">
              <option value="OPEN">OPEN</option>
              <option value="CLOSED">CLOSED</option>
            </select>
            {this.displayDescPolicy()}
          </div>
          <div className="button-container">
            <input type="submit" value="Create team" />
          </div>
        </form>
        {this.renderErrorMessage()}
      </div>
    );
  }
}

export default TeamCreation