import logo from './logo.svg';
import './App.css';
import { Component } from "react";
import { Grid } from "@mui/material";
import { useState } from "react";

function Header() {
  return (
    <div className="App">
      <header className="App-header">
        <img src={logo} className="App-logo" alt="logo" />
        <p>
          Edit <code>src/App.js</code> and save to reload.
        </p>
        <a
          className="App-link"
          href="https://reactjs.org"
          target="_blank"
          rel="noopener noreferrer"
        >
          Learn React
        </a>
      </header>
    </div>
  );
}

function Button(props) {
  return (
    <Grid item>
      <button onClick={props.onClick}>{props.value}</button>
    </Grid>
  );
}

function DisplayTournamentPanel() {
  return <h2>list of tournaments</h2>;
}

function CreateTeam() {
  const [name, setName] = useState("");
  const [type, setType] = useState("open");

  const handleChange = (event) => {
    setType(event.target.value);
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    alert(`The name you entered was: ${name}, ${type} to new members`);
  };

  return (
    <Grid
      container
      direction="column"
      justifyContent="space-between"
      alignItems="center"
    >
      <h2>Team creation</h2>
      <form onSubmit={handleSubmit}>
        <Grid item>
          <label>
            Enter new team name:
            <input
              type="text"
              value={name}
              onChange={(e) => setName(e.target.value)}
            />
          </label>
        </Grid>
        <Grid item>
          <select value={type} onChange={handleChange}>
            <option value="Open">Open</option>
            <option value="Closed">Closed</option>
          </select>
        </Grid>
        <input type="submit" />
      </form>
    </Grid>
  );
}

function MainPanel(props) {
  switch (props.view) {
    case 0:
      return <DisplayTournamentPanel />;
    case 1:
      return <CreateTeam />;
    case 2:
      return;
    default:
      return;
  }
}

class App extends Component {
  constructor(props) {
    super(props);
    this.state = {
      view: 0
    };
    this.view = 0;
  }
  render() {
    return (
      <div>
        <Header />
        <Grid
          container
          paddingTop={1}
          direction="row"
          columns={2}
          justifyContent="space-around"
          alignItems="stretch"
        >
          <Grid
            item
            xs={2}
            container
            spacing={1}
            direction="column"
            alignItems="center"
            justifyContent="flex-start"
          >
            <Button 
              value="Home" 
              onClick={() => this.setState({ view: 0 })} 
            />
            <Button
              value="Create Team"
              onClick={() => this.setState({ view: 1 })}
            />
          </Grid>
          <Grid item xs={10} container direction="column" alignItems="center">
            <MainPanel view={this.state.view} />
          </Grid>
        </Grid>
      </div>
    );
  }
}
export default App;
