import React from 'react';
import SignIn from './SignIn';
import postData, { checkEmail, checkPassword, checkUsername } from '../utils';
import { GoToErrorPage } from '../utils';
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
      view: "SignUp",
      badResponse: null,
      registered:false
    };
    this.handleChange = this.handleChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
    this.renderErrorMessage=this.renderErrorMessage.bind(this);
  }

  renderErrorMessage() {
    if (this.state.errorMessage === null) return;
    return (
      <p className='error'>{this.state.errorMessage}</p>
    );
  }

  handleChange(event) {
    if (event.target.name === "username") this.setState({ username: event.target.value });
    else if (event.target.name === "password") this.setState({ password: event.target.value });
    else if (event.target.name === "email") this.setState({ email: event.target.value });
    else if (event.target.name === "confirmpassword") this.setState({ confirmpassword: event.target.value });

  }

  handleSubmit(event) {
    event.preventDefault();
    if(!checkUsername(this.state.username)){
      this.setState({ errorMessage: "username must be from 3 to 20 characters long (3 and 20 are valid lengths), can contain only uppercase char, lowercase char and digits, can NOT start with a digit" })
      return;
     }
     if(!checkEmail(this.state.email)){
      this.setState({errorMessage: "invalid email address"});
      return;
     }
    if(!checkPassword(this.state.password) ){
      this.setState({ errorMessage: "password must be from 8 to 20 characters long (8 and 20 are valid lengths), can contain only uppercase char, lowercase char and digits, must contain at least one uppercase, one lowercase and one digit" })
      return;
    }
    if(!checkPassword(this.state.password) ){
      this.setState({ errorMessage: "password must be from 8 to 20 characters long (8 and 20 are valid lengths), can contain only uppercase char, lowercase char and digits, must contain at least one uppercase, one lowercase and one digit" })
      return;
    }
    
     
    if (this.state.password !== this.state.confirmpassword) {
      this.setState({ errorMessage: "confirm password is different from password!" })
      return;
    }

    let data = { username: this.state.username, password: this.state.password, email: this.state.email };
    postData("/authentication/register", data).then((response) => {
      if (response.status === 200) {
        if (response.result.result === "Registered") {
          this.setState({view:"SignIn",registered:true})
        }
        else if (response.result.result === "Email already taken") {
  
          this.setState({ errorMessage: "email already taken" })
        }
        else if (response.result.result === "Username already taken") {
          this.setState({ errorMessage: "username already taken" })
        }
      }
      else {
        this.setState({ errorMessage: "the server encountered an error" })
        this.setState({ badResponse: response.message });
      }
    }
    );
  }


  render() {
    if (this.state.errorMessage === "the server encountered an error") return (<GoToErrorPage path="/error" message={this.state.badResponse} />);
    if (this.state.view === "SignIn") return (<SignIn backHome={this.props.backHome} index={this.props.index} registered={this.state.registered} />);
    else if (this.state.view === "SignUp") return (
      <div className="main-panel">
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
          
          <button className="formbutton" onClick={() => this.setState({ view: "SignIn" })}> Sign in if you already have an account </button>
        </div>
        {this.renderErrorMessage()}
      </div>
    );
  }
}
export default SignUp;