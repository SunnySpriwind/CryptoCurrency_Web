<template>
  <el-container style="height: 100%; border: 1px solid #eee">
    <el-aside width="200px" style="background-color: rgb(238, 241, 246)">
      <el-menu :default-openeds="['1', '3']">
        <el-submenu index="1">
          <template slot="title"><i class="el-icon-message"></i>PersonPage</template>
          <el-menu-item-group>
            <template slot="title">My Account</template>
            <el-menu-item index="1-1" @click="navigateTo('/person')">Personal Page</el-menu-item>
          </el-menu-item-group>
          <el-menu-item-group title="Cryptocurrencies">
            <el-menu-item index="1-3">My assets</el-menu-item>
          </el-menu-item-group>
          <el-menu-item-group title="Go Back">
            <el-menu-item index="1-3" @click="goBack('/main')">Back</el-menu-item>
          </el-menu-item-group>
        </el-submenu>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header style="text-align: right; font-size: 12px">
        <span style="height:100px; width:100px">{{ username }}</span>
      </el-header>

      <el-main>

        <el-button type="primary" @click="showCreateDialog" style="margin-bottom: 20px; width:140px">Create Assets
        </el-button>

        <el-dialog :visible.sync="createDialogVisible" title="Create New Coin" width="30%">
          <el-form :model="newCoinForm">
            <el-form-item label="Coin Name">
              <el-select v-model="newCoinForm.coin" placeholder="Select coin name">
                <el-option label="bitcoin" value="bitcoin"></el-option>
                <el-option label="ethereum" value="ethereum"></el-option>
                <el-option label="binancecoin" value="binancecoin"></el-option>
              </el-select>
              <el-form-item label="Quantity">
                <el-input v-model="newCoinForm.quantity" type="number" placeholder="Enter quantity"></el-input>
              </el-form-item>
            </el-form-item>
          </el-form>
          <span slot="footer" class="dialog-footer">
    <el-button @click="createDialogVisible = false">Cancel</el-button>
    <el-button type="primary" @click="createNewCoin">Create</el-button>
  </span>
        </el-dialog>


        <el-button type="primary" @click="showPreferencesDialog"
                   style="margin-bottom: 20px; width:140px;margin-left: 20px;">Preferences
        </el-button>

        <el-dialog :visible.sync="preferencesDialogVisible" title="User Preferences" width="50%">
          <el-table :data="preferencesData">
            <el-table-column prop="userid" label="User ID" width="100"></el-table-column>
            <el-table-column prop="username" label="Username" width="150"></el-table-column>
            <el-table-column prop="currencyname" label="Coin Name" width="140"></el-table-column>
            <el-table-column prop="marketname" label="Market" width="140"></el-table-column>
            <el-table-column prop="selectcharts" label="Selected Charts" width="180"></el-table-column>
            <el-table-column label="Actions" width="120">
              <template slot-scope="scope">
                <el-button type="primary" @click="viewChartMap(scope.row)" style="width:80px">View</el-button>
              </template>
            </el-table-column>
          </el-table>
          <span slot="footer" class="dialog-footer">
    <el-button @click="preferencesDialogVisible = false">Close</el-button>
  </span>
        </el-dialog>

        <el-table :data="tableData">
          <el-table-column prop="coin" label="Coin" width="140"></el-table-column>
          <el-table-column prop="quantity" label="Quantity" width="120"></el-table-column>
          <el-table-column prop="price" label="Price (CoinGecko/USD)" width="120"></el-table-column>
          <el-table-column prop="value" label="Total Value (USD)" width="120"></el-table-column>
          <el-table-column prop="priceadd" label="Price (CoinMarketCap/USD)" width="120"></el-table-column>
          <el-table-column prop="valueadd" label="Total Value (USD)" width="120"></el-table-column>
          <el-table-column prop="detail" label="Detail">
            <template slot-scope="scope">
              <el-button type="primary" @click="showDetails(scope.row)" style="width:80px">Details</el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-dialog :visible.sync="dialogVisible" title="Details" width="50%">
          <el-table :data="dialogData">
            <el-table-column prop="id" label="ID" width="50"></el-table-column>
            <el-table-column prop="coinid" label="Coin ID" width="100"></el-table-column>
            <el-table-column prop="coin" label="Coin" width="100"></el-table-column>
            <el-table-column prop="owner" label="Owner" width="100"></el-table-column>
          </el-table>
          <span slot="footer" class="dialog-footer">
            <el-button @click="dialogVisible = false">Close</el-button>
          </span>
        </el-dialog>

        <div id="coin-pie-chart" style="width: 50%; height: 400px; float: left; margin-top: 20px;"></div>
        <div id="coin-line-chart" style="width: 50%; height: 400px; float: left; margin-top: 20px;"></div>
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
import {jwtDecode} from 'jwt-decode';
import request from '@/utils/request';
import * as echarts from 'echarts';

export default {
  name: 'AssetView',
  data() {
    return {
      token: '',
      username: '',
      userId: '',
      tableData: [],
      dialogVisible: false,
      dialogData: [],
      coinPieChart: null,
      createDialogVisible: false,
      preferencesDialogVisible: false,
      preferencesData: [],
      newCoinForm: {
        coin: '',
        quantity: null,
      },
    };
  },

  mounted() {
    this.token = this.getToken();
    this.username = this.getUsernameFromToken(this.token);
    this.userId = this.getUserIdFromToken(this.token)
    this.fetchCoinData();
    this.coinPieChart = echarts.init(document.getElementById('coin-pie-chart'));
    this.coinLineChart = echarts.init(document.getElementById('coin-line-chart'));
    this.intervalId = setInterval(() => {
      this.fetchCoinData();
    }, 60000);
  },

  beforeDestroy() {
    if (this.intervalId) {
      clearInterval(this.intervalId);
    }
  },

  methods: {
    getToken() {
      return localStorage.getItem('token') || '';
    },

    getUsernameFromToken(token) {
      try {
        const decoded = jwtDecode(token);
        return decoded.name;
      } catch (error) {
        console.error('Invalid token', error);
        return '';
      }
    },

    getUserIdFromToken(token) {
      console.log("id_token")
      try {
        const decoded = jwtDecode(token);
        return parseInt(decoded.id, 10);

      } catch (error) {
        console.error('Invalid token', error);
        return '';
      }
    },

    async fetchPrice(coinName, market) {
      try {
        const response = await request.get('/realtime/general', {
          params: {name: coinName, market},
        });
        const prices = response.data;
        if (prices.length > 0) {
          const latestPrice = prices[prices.length - 1].price;
          return parseFloat(latestPrice);
        }
        return 0;
      } catch (error) {
        console.error(`Failed to fetch price for ${coinName} in ${market}`, error);
        return 0;
      }
    },

    async fetchCoinData() {

      const params = {
        name: this.username,
      };
      try {
        const response = await request.get('/getcoin', {params});

        if (response.code === 1) {
          await this.processCoinData(response.data);
          this.updateCoinPieChart();
          this.updateCoinLineChart();
        } else {
          console.error('Unexpected response code:', response.code);
        }
      } catch (error) {
        console.error('Failed to fetch coin data', error);
      }
    },

    async processCoinData(data) {

      const coinMap = {};

      for (const item of data) {
        if (!coinMap[item.coin]) {
          coinMap[item.coin] = {
            coin: item.coin,
            quantity: 0,
            price: 0,
            value: 0,
          };
        }

        coinMap[item.coin].quantity += 1;

        const priceCoinGecko = await this.fetchPrice(item.coin, 'CoinGecko');
        const priceCoinMarketCap = await this.fetchPrice(item.coin, 'CoinMarketCap');

        coinMap[item.coin].price = priceCoinGecko;
        coinMap[item.coin].value = coinMap[item.coin].quantity * priceCoinGecko;
        coinMap[item.coin].priceadd = priceCoinMarketCap;
        coinMap[item.coin].valueadd = coinMap[item.coin].quantity * priceCoinMarketCap;
      }

      this.tableData = Object.values(coinMap);
    },

    showDetails(row) {

      this.dialogVisible = true;
      this.fetchCoinDetails(row.coin);
    },

    fetchCoinDetails(coin) {
      const params = {
        name: this.username,
      };
      request
          .get('/getcoin', {params})
          .then((response) => {

            if (response.code === 1) {
              this.dialogData = response.data.filter((item) => item.coin === coin);
            } else {
              console.error('Unexpected response code:', response.code);
            }
          })
          .catch((error) => {
            console.error('Failed to fetch coin details', error);
          });
    },

    showCreateDialog() {
      this.createDialogVisible = true;
    },

    createNewCoin() {
      if (!this.newCoinForm.coin || !this.newCoinForm.quantity) {
        this.$message.error('Please enter both coin and quantity');
        return;
      }

      const coin = {
        coin: this.newCoinForm.coin,
        owner: this.username,
      };

      const quantity = this.newCoinForm.quantity;

      const params = new URLSearchParams();
      params.append('number', quantity);

      request
          .post('/createcoin?' + params.toString(), coin)
          .then((response) => {
            if (response.code === 1) {
              this.$message.success('Coin created successfully');
              this.fetchCoinData();
              this.createDialogVisible = false;
              this.newCoinForm.coin = '';
              this.newCoinForm.quantity = null;
            } else {
              this.$message.error('Failed to create coin');
            }
          })
          .catch((error) => {
            console.error('Failed to create coin', error);
            this.$message.error('Error occurred while creating coin');
          });
    },

    updateCoinPieChart() {
      const pieData = this.tableData.map(item => ({
        name: item.coin,
        value: item.quantity
      }));

      const option = {
        title: {
          text: 'Coin Distribution',
          left: 'center'
        },
        tooltip: {
          trigger: 'item'
        },
        legend: {
          orient: 'vertical',
          left: 'left'
        },
        series: [
          {
            name: 'Quantity',
            type: 'pie',
            radius: '50%',
            data: pieData,
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      };

      this.coinPieChart.setOption(option);
    },

    updateCoinLineChart() {
      const categories = this.tableData.map(item => item.coin); // 将货币名作为 x 轴的数据
      const seriesData = []; // 用来保存每种货币的 value 数据

      // 构建每种货币的 series 数据
      this.tableData.forEach(item => {
        seriesData.push({
          name: item.coin,
          type: 'line',
          data: [
            item.value,      // 从表格中的 value 字段取值
            item.valueadd    // 如果需要显示其他数据（例如 valueadd），可以添加更多数据点
          ]
        });
      });

      const option = {
        title: {
          text: 'Cryptocurrency Portfolio Value',
          left: 'center'
        },
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: categories,
          top: 'bottom'
        },
        xAxis: {
          type: 'category',
          data: categories // 使用货币名作为 x 轴数据
        },
        yAxis: {
          type: 'value',
          name: 'Value (USD)'
        },
        series: seriesData // 包含每种货币的 value 数据
      };

      this.coinLineChart.setOption(option);
    },

    showPreferencesDialog() {
      this.fetchPreferences();
      this.preferencesDialogVisible = true;
    },

    async fetchPreferences() {
      console.log("thisid:" + this.userId)
      try {
        const response = await request.get(`/getcharts`, {
          params: {id: this.userId}
        });

        if (response.code === 1) {
          this.preferencesData = response.data;
          console.log("preference: " + JSON.stringify(this.preferencesData, null, 2));
        } else {
          console.error('Unexpected response code:', response.code);
        }
      } catch (error) {
        console.error('Failed to fetch preferences', error);
      }
    },

    viewChartMap(row) {
      this.$router.push({
        path: '/chartmap',
        query: {
          coinname: row.currencyname,
          market: row.marketname,
          charts: row.selectcharts
        }
      }).then(() => {
        window.location.reload();
      });
    },

    navigateTo(route) {
      this.$router.push(route).then(() => {
        window.location.reload();
      });
    },

    goBack(route) {
      this.$router.push(route).then(() => {
        window.location.reload();
      });
    },
  },
};
</script>
