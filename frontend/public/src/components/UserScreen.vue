<template>
  <div class="user-screen">
    <h1>User Management</h1>

    <!-- User Form -->
    <form @submit.prevent="createUser">
      <div>
        <label for="firstName">First Name:</label>
        <input type="text" id="firstName" v-model="newUser.firstName" required />
      </div>
      <div>
        <label for="lastName">Last Name:</label>
        <input type="text" id="lastName" v-model="newUser.lastName" required />
      </div>
      <div>
        <label for="email">Email:</label>
        <input type="email" id="email" v-model="newUser.email" required />
      </div>
      <div>
        <label for="phone">Phone:</label>
        <input type="text" id="phone" v-model="newUser.phone" />
      </div>
      <button type="submit">Create User</button>
    </form>

    <!-- User List -->
    <h2>Existing Users</h2>
    <table>
      <thead>
      <tr>
        <th>ID</th>
        <th>First Name</th>
        <th>Last Name</th>
        <th>Email</th>
        <th>Phone</th>
        <th>Actions</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="user in users" :key="user.id">
        <td>{{ user.id }}</td>
        <td>{{ user.firstName }}</td>
        <td>{{ user.lastName }}</td>
        <td>{{ user.email }}</td>
        <td>{{ user.phone }}</td>
        <td>
          <button @click="deleteUser(user.id)">Delete</button>
        </td>
      </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "UserScreen",
  data() {
    return {
      users: [],
      newUser: {
        firstName: "",
        lastName: "",
        email: "",
        phone: "",
      },
    };
  },
  methods: {
    async fetchUsers() {
      try {
        const response = await axios.get("/api/v1/users");
        this.users = response.data;
      } catch (error) {
        console.error("Error fetching users:", error);
      }
    },
    async createUser() {
      try {
        const response = await axios.post("/api/v1/users", this.newUser);
        this.users.push(response.data);
        this.newUser = { firstName: "", lastName: "", email: "", phone: "" };
      } catch (error) {
        console.error("Error creating user:", error);
      }
    },
    async deleteUser(userId) {
      try {
        await axios.delete(`/api/v1/users/${userId}`);
        this.users = this.users.filter((user) => user.id !== userId);
      } catch (error) {
        console.error("Error deleting user:", error);
      }
    },
  },
  mounted() {
    this.fetchUsers();
  },
};
</script>

<style scoped>
.user-screen {
  max-width: 800px;
  margin: 0 auto;
}
form {
  margin-bottom: 20px;
}
form div {
  margin-bottom: 10px;
}
table {
  width: 100%;
  border-collapse: collapse;
}
table th, table td {
  border: 1px solid #ddd;
  padding: 8px;
}
table th {
  background-color: #f4f4f4;
}
</style>