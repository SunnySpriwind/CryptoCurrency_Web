<template>
  <el-container style="height: 500px; border: 1px solid #eee">
    <el-aside width="200px" style="background-color: rgb(238, 241, 246)">
      <el-menu :default-openeds="['1', '3']">
        <el-submenu index="1">
          <template slot="title"><i class="el-icon-message"></i>PersonPage</template>
          <el-menu-item-group>
            <template slot="title">My Account</template>
            <el-menu-item index="1-1">Personal Page</el-menu-item>
          </el-menu-item-group>
          <el-menu-item-group title="Cryptocurrencies">
            <el-menu-item index="1-3" @click="navigateTo('/asset')">My assets</el-menu-item>
          </el-menu-item-group>
          <el-menu-item-group title="Go Back">
            <el-menu-item index="1-3" @click="navigateTo('/main')">Back</el-menu-item>
          </el-menu-item-group>
        </el-submenu>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header style="text-align: right; font-size: 12px">
        <span style="height:100px; width:100px">{{ username }}</span>
      </el-header>

      <el-main>
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>User Information</span>
            <el-button style="float: right;" type="primary" @click="showEditDialog">Edit</el-button>
            <el-button style="float: right; margin-right: 10px;" type="primary" @click="showChangePasswordDialog">Change
              Password
            </el-button>
          </div>
          <div v-if="user">
            <p><strong>Username:</strong> {{ user.name }}</p>
            <p><strong>Email:</strong> {{ user.email }}</p>
          </div>
        </el-card>

        <el-dialog title="Edit User Information" :visible.sync="editDialogVisible">
          <el-form :model="editForm">
            <el-form-item label="Username">
              <el-input v-model="editForm.name"></el-input>
            </el-form-item>
            <el-form-item label="Email">
              <el-input v-model="editForm.email"></el-input>
            </el-form-item>
          </el-form>
          <span slot="footer" class="dialog-footer">
            <el-button @click="editDialogVisible = false">Cancel</el-button>
            <el-button type="primary" @click="saveUserData">Save</el-button>
          </span>
        </el-dialog>

        <el-dialog title="Change Password" :visible.sync="changePasswordDialogVisible">
          <el-form :model="passwordForm">
            <el-form-item label="New Password">
              <el-input type="password" v-model="passwordForm.newPassword"></el-input>
            </el-form-item>
            <el-form-item label="Confirm New Password">
              <el-input type="password" v-model="passwordForm.confirmNewPassword"></el-input>
            </el-form-item>
          </el-form>
          <span slot="footer" class="dialog-footer">
            <el-button @click="changePasswordDialogVisible = false">Cancel</el-button>
            <el-button type="primary" @click="changePassword">Change Password</el-button>
          </span>
        </el-dialog>

      </el-main>
    </el-container>
  </el-container>
</template>

<style>
.el-header {
  background-color: #B3C0D1;
  color: #333;
  line-height: 60px;
}

.el-aside {
  color: #333;
}
</style>

<script>
import {jwtDecode} from "jwt-decode";
import request from '@/utils/request';

export default {
  name: 'PersonView',
  data() {
    return {
      token: '',
      username: '',
      editDialogVisible: false,
      changePasswordDialogVisible: false,
      user: null,
      editForm: {
        name: '',
        email: ''
      },

      passwordForm: {
        oldPassword: '',
        newPassword: '',
        confirmNewPassword: ''
      }
    }
  },

  mounted() {
    this.token = this.getToken();
    const decodedToken = this.getUsernameFromToken(this.token);
    this.username = decodedToken.name;
    this.fetchUserInfo(decodedToken.id);
  },

  methods: {
    getToken() {
      return localStorage.getItem('token') || '';
    },

    getUserIdFromToken(token) {
      try {
        const decoded = jwtDecode(token);
        return decoded.id;
      } catch (error) {
        console.error('Invalid token', error);
        return null;
      }
    },

    getUsernameFromToken(token) {
      try {
        return jwtDecode(token);
      } catch (error) {
        console.error('Invalid token', error);
        return {};
      }
    },

    fetchUserInfo(userId) {

      const params = {id: userId};
      request.get('/getuser', {params})
          .then(response => {
            if (response.code === 1) {

              this.user = response.data;
            } else {
              console.error('Failed to fetch user info:', response.message);
            }
          })
          .catch(error => {
            console.error('Error fetching user info:', error);
          });
    },

    showEditDialog() {
      this.editDialogVisible = true;
    },

    showChangePasswordDialog() {
      this.changePasswordDialogVisible = true;
    },

    saveUserData() {
      const userId = this.getUserIdFromToken(this.token);
      const user = {
        id: userId,
        name: this.editForm.name,
        email: this.editForm.email,
      };



      request.post('/save', user)
          .then(response => {
            if (response.code === 1) {
              this.user.name = this.editForm.name;
              this.user.email = this.editForm.email;
              this.editDialogVisible = false;
              this.$message.success('User information updated successfully');
            } else {
              console.error('Unexpected response data:', response);
              this.$message.error('Failed to update user information');
            }
          })
          .catch(error => {
            console.error('Failed to save user info:', error);
            this.$message.error('Failed to update user information');
          });
    },

    changePassword() {
      if (this.passwordForm.newPassword !== this.passwordForm.confirmNewPassword) {
        this.$message.error('New password and confirm password do not match');
        return;
      }

      const userId = this.getUserIdFromToken(this.token);
      const user = {
        id: userId,
        password: this.passwordForm.newPassword,
      };

      request.post('/savepassword', user)
          .then(response => {
            if (response.code === 1) {
              this.changePasswordDialogVisible = false;
              this.$message.success('Password updated successfully');
            } else {
              console.error('Unexpected response data:', response);
              this.$message.error('Failed to update password');
            }
          })
          .catch(error => {
            console.error('Failed to change password:', error);
            this.$message.error('Failed to update password');
          });
    },

    navigateTo(route) {
      this.$router.push(route).then(() => {
        window.location.reload();
      });
    },
  }
};
</script>