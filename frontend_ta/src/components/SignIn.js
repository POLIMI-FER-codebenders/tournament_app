import React from 'react';
import { postForm } from '../utils';
import SignUp from './signUp';
import "../styles/SignInUp.css";
import '../styles/App.css';
import { GoToErrorPage } from '../utils';


class SignIn extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      errorMessage: null,
      isSubmitted: false,
      username: '',
      password: '',
      view: "SignIn",
      badResponse: null

    };
    this.handleChange = this.handleChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
  }

  renderErrorMessage() {
    if (this.state.errorMessage == null) return;
    return (
      <p>{this.state.errorMessage}</p>
    );
  }

  handleChange(event) {
    if (event.target.name == "username") this.setState({ username: event.target.value });
    else if (event.target.name == "password") this.setState({ password: event.target.value });
  }

  handleSubmit(event) {
    event.preventDefault();
    let data = new FormData();
    data.append("username", this.state.username);
    data.append("password", this.state.password);
    let url = "/authentication/login";
    let username = this.state.username;

    postForm(url, data)
      .then((response) => {
        if (response.status == 200) {
          if (response.result) {
            this.setState({ errorMessage: null })
            sessionStorage.setItem("username", username);
            this.props.backHome(this.props.index, true);
          }
          else {
            this.setState({ errorMessage: "invalid credentials" })
          }
        }
        else {
          this.setState({ errorMessage: "the server encountered an error" })
          this.setState({ badResponse: response.message })
        }
      });
  }


  render() {
    let nextcomponent;
    if (this.state.errorMessage == "the server encountered an error") return (<GoToErrorPage path="/error" message={this.state.badResponse} />);
    if (this.state.view == "SignUp") nextcomponent = (<SignUp backHome={this.props.backHome} index={this.props.index} />);
    let registrationmessage;
    if (this.props.registered) registrationmessage = <p>Registration successfull, please login</p>
    if (this.state.view == "SignIn") nextcomponent = (
      <div className="main-panel">
        {registrationmessage}
        <h2>Sign In</h2>
        <div className="form">
          <form onSubmit={this.handleSubmit}>
            <div className="input-container">
              <label htmlFor="usernamelogin">Username </label>
              <input type="text" name="username" id="usernamelogin" value={this.state.username} onChange={this.handleChange} required />
            </div>
            <div className="input-container">
              <label htmlFor="passwordlogin">Password </label>
              <input type="password" name="password" id="passwordlogin" value={this.state.password} onChange={this.handleChange} required />

            </div>
            <div className="button-container">
              <input type="submit" value="Sign in" />
            </div>
          </form>
          {this.renderErrorMessage()}
          <button className="formbutton" onClick={() => this.setState({ view: "SignUp" })}> Create Account </button>
        </div>
      </div>
    );
    return nextcomponent;
  }
}
export default SignIn;
