<template>
  <div>
    <el-container>
      <el-header>
        <div class="header">
          <!--logo-->
          <img src="@/assets/AQueryLogo.jpg" class="logo" alt="" style="width:100px"/>

          <!--menu-->
          <el-menu :default-active="$router.path" router class="menu" mode="horizontal">
            <el-menu-item @click="navigateTo('/main/firstpage')">First Page</el-menu-item>
            <el-submenu index="2">
              <template slot="title">Query</template>
              <el-menu-item @click="navigateTo('/main/realtime')">Real-time Data</el-menu-item>
              <el-menu-item @click="navigateTo('/main/historytime')">Historical Data</el-menu-item>
              <el-menu-item @click="navigateTo('/main/comparing')">Comparing</el-menu-item>
              <el-menu-item @click="navigateTo('/chartmap')">ChartMap</el-menu-item>
            </el-submenu>
            <el-menu-item index="3" @click="warning()">Trading</el-menu-item>
          </el-menu>


          <!--user button-->
          <div class="user-profile">
            <el-dropdown @command="handleCommand">
              <div class="el-dropdown-link">
                <img src="https://via.placeholder.com/100x100" alt="User Avatar" class="user-avatar">
                <span class="username">{{ username }}</span>
              </div>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="account">My Account</el-dropdown-item>
                <el-dropdown-item command="assets">Assets</el-dropdown-item>
                <el-dropdown-item command="logout">Log Out</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>

        </div>
      </el-header>

      <el-main>
        <router-view/>
      </el-main>

      <el-footer>
      </el-footer>
    </el-container>
  </div>
</template>

<style>

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  height: 80px;
  padding: 0 20px;
}

.logo {
  height: 60px;
  width: 60px;
  margin-right: 20px;
}

.menu {
  flex: 1;
  display: flex;
  justify-content: flex-start;

}

.el-menu-item, .el-submenu__title {
  padding: 0 20px;
  font-size: 18px;
  font-weight: bold;
}

.el-dropdown-link {
  cursor: pointer;
  color: #409EFF;

}

.user-profile {
  display: inline-block;
  position: relative;
  margin-right: 30px;
}

.user-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  cursor: pointer;
}

.username {
  margin-left: 5px;
  font-size: 16px;
  line-height: 40px;
  cursor: pointer;
}
</style>

<script>
import {jwtDecode} from 'jwt-decode';

export default {
  name: 'MainView',
  data() {
    return {
      username: ''
    };
  },

  mounted() {
    this.parseJWT()
  },

  methods: {
    // To parse JWT.
    parseJWT() {
      const token = localStorage.getItem('token');

      if (token) {
        const decodedToken = jwtDecode(token);

        this.username = decodedToken.name;
      }

    },

    // To warn user and go back to first page.
    warning() {
      alert("This functionality is not open");
      this.navigateTo("/main/firstpage");
    },

    // To jump to target page.
    navigateTo(route) {
      if (this.$route.path !== route) {
        this.$router.push(route).then(() => {
          window.location.reload();
        }).catch(err => {
          if (err.name !== 'NavigationDuplicated') {
            console.error(err);
          }
        });
      }
    },

    // For dropdown functionality
    handleCommand(command) {

      if (command === 'logout') {
        localStorage.clear();
        this.$router.push('/login');
      } else if (command === 'account') {
        this.$router.push('/person');
      } else if (command === 'assets') {
        this.$router.push('/asset');
      }
    },
  }
}
</script>