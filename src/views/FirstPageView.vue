<template>
  <div>

    <div class="nothing"></div>

    <div class="fullscreen-carousel">
      <el-carousel :interval="5000" arrow="always" style="height: 80vh;">
        <el-carousel-item v-for="(item, index) in carouselData" :key="index">
          <img :src="item.imageUrl" alt="Image" class="carousel-image"/>
        </el-carousel-item>
      </el-carousel>
    </div>

    <div class="nothing"></div>


    <div class="crypto-platform">
      <h1 class="main-title">Have a nice day</h1>

      <div class="stats">
        <div class="stat-item">
          <h2>xxM+</h2>
          <p>Users</p>
        </div>
        <div class="stat-item">
          <h2>xx+</h2>
          <p>Country</p>
        </div>
        <div class="stat-item">
          <h2>xx+</h2>
          <p>Market</p>
        </div>
      </div>

      <div class="nothing"></div>

      <div class="content-wrapper">
        <div class="platform-preview">
          <img src="@/assets/Picture4.jpg" alt="Desktop Preview" class="desktop-preview">
          <img src="@/assets/Picture5.jpg" alt="Mobile Preview" class="mobile-preview">
        </div>

        <div class="platform-info">
          <h3>Data</h3>
          <h2>If you want to query data</h2>
          <ol>
            <li>Real-time</li>
            <li>Historical</li>
            <li>Comparing two different coins</li>
          </ol>
          <el-button type="primary" class="join-button" @click="navigateTo('/main/realtime')">Click here</el-button>
        </div>
      </div>
    </div>


    <div class="why-kraken">
      <h2 class="title">Our website</h2>

      <div class="reasons">
        <div class="reason">
          <div class="icon">

            <svg width="150" height="150" viewBox="0 0 50 50" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="10" cy="25" r="5" fill="#FFA07A"/>
              <circle cx="25" cy="25" r="5" fill="#FFA07A"/>
              <circle cx="40" cy="25" r="5" fill="#FFA07A"/>
              <path d="M10 25L25 25L40 25" stroke="#8A2BE2" stroke-width="2"/>
            </svg>
          </div>
          <h1>Visualization</h1>
          <p><span class="highlight">We show many charts for displaying the infomation of Cryptocurrency</span></p>
        </div>

        <div class="reason">
          <div class="icon">
            <svg width="150" height="150" viewBox="0 0 50 50" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="25" cy="25" r="20" stroke="#8A2BE2" stroke-width="2"/>
              <path d="M35 35L45 45" stroke="#8A2BE2" stroke-width="2"/>
            </svg>
          </div>
          <h1>Comparing</h1>
          <p><span class="highlight">We provide tool to compare different currencies</span><br></p>
        </div>

        <div class="reason">
          <div class="icon">
            <svg width="150" height="150" viewBox="0 0 50 50" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M25 5V25L40 40" stroke="#8A2BE2" stroke-width="2"/>
              <circle cx="25" cy="25" r="20" stroke="#8A2BE2" stroke-width="2"/>
            </svg>
          </div>
          <h1>Service</h1>
          <p>If you have any problem<br>contact us <br>from problem</p>
        </div>
      </div>
    </div>
  </div>
</template>


<style>

.nothing {
  margin-top: 100px;
}


.fullscreen-carousel {
  height: 100px;
  width: 80%;
  margin: 0 auto;
}

.carousel-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}


.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  margin-bottom: 80px;
  padding: 0 20px;
}

.user-profile {
  position: relative;
}

.user-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  cursor: pointer;
}

.el-dropdown-link {
  display: flex;
  align-items: center;
}

.crypto-platform {
  font-family: Arial, sans-serif;
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
}

.main-title {
  text-align: center;
  font-size: 24px;
  margin-bottom: 40px;
}

.stats {
  display: flex;
  justify-content: space-around;
  margin-bottom: 60px;
}

.stat-item {
  text-align: center;
}

.stat-item h2 {
  font-size: 36px;
  margin-bottom: 10px;
}

.stat-item p {
  font-size: 16px;
  color: #666;
}

.content-wrapper {
  display: flex;
  align-items: center;
  gap: 40px;
}

.platform-preview {
  flex: 1;
  position: relative;
}

.desktop-preview {
  width: 100%;
  max-width: 500px;
}

.mobile-preview {
  position: absolute;
  right: 0;
  bottom: -50px;
  width: 60%;
  max-width: 400px;
}

.platform-info {
  flex: 1;
}

.platform-info h3 {
  color: #666;
  font-size: 18px;
  margin-bottom: 10px;
}

.platform-info h2 {
  font-size: 28px;
  margin-bottom: 20px;
}

.platform-info ol {
  padding-left: 20px;
  margin-bottom: 30px;
}

.platform-info li {
  margin-bottom: 10px;
  font-size: 16px;
}

.buy-crypto-btn {
  padding: 12px 24px;
  font-size: 18px;
}

@media (max-width: 768px) {
  .content-wrapper {
    flex-direction: column;
  }

  .platform-preview, .platform-info {
    height: 100%;
    width: 100%;
  }
}

.why-kraken {
  font-family: Arial, sans-serif;
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
  text-align: center;
}

.title {
  font-size: 28px;
  margin-bottom: 80px;
  margin-top: 80px;
}

.reasons {
  display: flex;
  justify-content: space-around;
  margin-bottom: 40px;
}

.reason {
  flex: 1;
  margin: 0 15px;
}

.icon {
  margin-bottom: 80px;
}

.reason h3 {
  font-size: 20px;
  margin-bottom: 10px;
}

.reason p {
  font-size: 14px;
  line-height: 1.5;
}

.highlight {
  color: #8A2BE2;
}

.join-button {
  padding: 12px 24px;
  font-size: 18px;
  background-color: #8A2BE2;
  border-color: #8A2BE2;
}

.join-button:hover, .join-button:focus {
  background-color: #9B30FF;
  border-color: #9B30FF;
}

@media (max-width: 768px) {
  .reasons {
    flex-direction: column;
  }

  .reason {
    margin-bottom: 30px;
  }
}
</style>

<script>
import Picture1 from '@/assets/Picture1.jpg'

export default {
  name: 'FirstPage',
  data() {
    return {
      carouselData: [
        {imageUrl: Picture1},
        {imageUrl: Picture1},
        {imageUrl: Picture1},
        {imageUrl: Picture1},
      ],
    };
  },

  mounted() {
  },

  methods: {

    // To jump to target page.
    navigateTo(route) {
      this.$router.push(route).then(() => {
        window.location.reload();
      });
    },

  },
};
</script>