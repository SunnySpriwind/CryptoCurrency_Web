<template>
  <div class="login-container">
    <div class="login-box">
      <h1>AQuery</h1>
      <h2>Login</h2>
      <form @submit.prevent="handleLogin">
        <div class="input-group">
          <label for="username">Username</label>
          <input type="text" id="username" v-model="name" required placeholder="Enter your Username"/>
        </div>
        <div class="input-group">
          <label for="password">Password</label>
          <input type="password" id="password" v-model="password" required placeholder="Enter your password"/>
        </div>
        <div class="button-group">
          <button type="submit" class="login-btn">Login</button>
          <button type="button" class="signin-btn" @click="gotoregister">Sign in</button>
        </div>
      </form>
    </div>
  </div>
</template>

<style>
html, body {
  height: 100%;
  width: 100%;
  margin: 0;
  padding: 0;
  overflow: hidden;
}

.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  width: 100vw;
  background-image: url("@/assets/background.jpg");
  background-size: cover;
  background-position: center;
  background-attachment: fixed;
}

.login-box {
  background-color: rgba(255, 255, 255, 0.9);
  padding: 40px;
  border-radius: 10px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  width: 500px;
  text-align: center;
  margin-top: -50px;
}

.login-box h2 {
  margin-bottom: 20px;
}

.input-group {
  margin-bottom: 20px;
  text-align: left;
}

.input-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

.input-group input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  box-sizing: border-box;
}

.button-group {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

button {
  width: 48%;
  padding: 15px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 16px;
}

button:hover {
  background-color: #0056b3;
}

.signin-btn {
  background-color: white;
  color: #007bff;
  border: 1px solid #007bff;
}

.signin-btn:hover {
  background-color: #f0f0f0;
}
</style>

<script>
import request from "@/utils/request";

export default {
  name: 'LoginView',
  data() {
    return {
      name: '',
      password: ''
    };
  },

  created() {
  },

  methods: {

    // To send login request .
    handleLogin() {

      const user = {
        name: this.name,
        password: this.password
      };

      request.post("/login", user).then(response => {
        if (response.code === 1) {
          this.$message({
            message: 'Login successfully',
            type: 'success'
          });
          localStorage.setItem("token", JSON.stringify(response.data));
          this.$router.push("/main");
        } else {
          this.$message({
            message: response.message,
            type: 'error'
          });
        }
      })
    },

    // To jump to register page.
    gotoregister() {
      this.$router.push("/register");
    }
  }
}
</script>