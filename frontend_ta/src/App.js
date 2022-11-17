import logo from './logo.svg';
import './styles/App.css';
import './styles/forms.css';
import { Component } from "react";
import SignIn from './components/SignIn';
import { CreateTeam } from './components/TeamManagement';
import { DisplayTournament } from './components/DisplayTournament'; 
import ManageTeams from './components/TeamManagement';

function Header() {
  return (
      <header className="App-header">
        <p>Welcome to tournament application of Code Defenders web game!</p>
      </header>
  );
}

function MainPanel(props) {
  switch (props.view) {
    case 0:
      return <DisplayTournament />;
    case 1:
      return <SignIn backHome={props.backHome} />
    case 2:
      return <CreateTeam />;
    case 3:
      return <ManageTeams />;
    default:
      return <DisplayTournament />;
  }
}

class App extends Component {
  constructor(props) {
    super(props);
    this.state = {
      view: 0
    };
    this.backHome= this.backHome.bind(this);    
  }
   backHome(){
   this.setState({view:0});
 }
 
  
  render() {
    return (
      <div>
        <Header />
        <div class="main-container">
          <div class="button-container">
            <button class="item" 
              onClick={() => this.setState({ view: 0 })}>
              Home
            </button>
            <button class="item" 
              onClick={() => this.setState({ view: 1 })}>
              Sign In
            </button>
            <button class="item" 
              onClick={() => this.setState({ view: 2 })}>
              Create Team
            </button>
            <button class="item" 
              onClick={() => this.setState({ view: 3 })}>
              Manage Teams
            </button>
          </div>
          <MainPanel view={this.state.view} backHome={this.backHome} />
          <div class="item"></div>
        </div>
      </div>
    );
  }
}
export default App;
