import React from 'react';
import SignIn from './SignIn';
import postData from '../utils';
class SignUp extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      errorMessage: null,
      isSubmitted: false,
      username: '',
      password: '',
      confirmpassword: '',
      email: '',
      view: "SignUp"
    };
    this.handleChange = this.handleChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
  }

  renderErrorMessage() {
    if (this.state.errorMessage == null) return;
    return (
      <p >{this.state.errorMessage}</p>
    );
  }

  handleChange(event) {
    if (event.target.name == "username") this.setState({ username: event.target.value });
    else if (event.target.name == "password") this.setState({ password: event.target.value });
    else if (event.target.name == "email") this.setState({ email: event.target.value });
    else if (event.target.name == "confirmpassword") this.setState({ confirmpassword: event.target.value });

  }

  handleSubmit(event) {
    event.preventDefault();
    if (this.state.password != this.state.confirmpassword) {
      this.setState({ errorMessage: "confirm password is different from password!" })
      return;
    }
    let data = { username: this.state.username, password: this.state.password, email: this.state.email };
    let url = "http://localhost:8080/authentication/register"
    let username = this.state.username;
    postData(url, data).then((response) => {
      if (response.status == 200) {
        if (response.result == "Registered") {
          this.setState({ errorMessage: null })
          sessionStorage.setItem("username", username)
          this.props.backHome();
        }
        else if (response.result == "Email already taken") {
          this.setState({ errorMessage: "email already taken" })
        }
        else if (response.result == "Username already taken") {
          this.setState({ errorMessage: "username already taken" })
        }
      }
      else {
        this.setState({ errorMessage: "the server encountered an error" })
      }
    }
    );




  }


  render() {
    if (this.state.view == "SignIn") return (<SignIn />);
    else if (this.state.view == "SignUp") return (
      <div className="app">
        <div className="login-form">
          <h2>Sign Up</h2>
          <div className="form">
            <form onSubmit={this.handleSubmit}>
              <div className="input-container">
                <label htmlFor="usernameregister">Username </label>
                <input type="text" name="username" id="usernameregister" value={this.state.username} onChange={this.handleChange} required />
              </div>
              <div className="input-container">
                <label htmlFor="emailregister">Email </label>
                <input type="text" name="email" id="emailregister" value={this.state.email} onChange={this.handleChange} required />
              </div>
              <div className="input-container">
                <label htmlFor="passwordregister">Password </label>
                <input type="password" name="password" id="passwordregister" value={this.state.password} onChange={this.handleChange} required />
              </div>
              <div className="input-container">
                <label htmlFor="confirmpasswordregister">Confirm Password </label>
                <input type="password" name="confirmpassword" id="confirmpasswordregister" value={this.state.confirmpassword} onChange={this.handleChange} required />
              </div>
              <div className="button-container">
                <input type="submit" value="Sign up" />
              </div>
            </form>
            {this.renderErrorMessage()}
            <button className="formbutton" onClick={() => this.setState({ view: "SignIn" })}> Sign in if you already have an account </button>
          </div>
        </div>
      </div>
    );
  }
}
export default SignUp;