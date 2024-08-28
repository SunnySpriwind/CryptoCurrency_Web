<template>
  <div class="container">
    <div class="chart-area">
      <div class="search-area">
        <div class="panel">
          <div class="inline-elements">
            <label>Coin: </label>
            <el-autocomplete
                class="input"
                v-model="coinname"
                :fetch-suggestions="querySearch"
                placeholder="Enter the name of coin"
            ></el-autocomplete>
            <label>Market: </label>
            <el-select v-model="market" class="input" placeholder="Select market">
              <el-option label="CoinGecko" value="CoinGecko"></el-option>
              <el-option label="CoinMarketCap" value="CoinMarketCap"></el-option>
            </el-select>
          </div>
          <div class="block">
            <span class="demonstration">Time: </span>
            <el-date-picker
                v-model="value1"
                type="daterange"
                range-separator="To"
                start-placeholder="Start"
                end-placeholder="End"
                class="date-picker"
            />
          </div>
          <el-button class="button" @click="search">Search</el-button>
        </div>
      </div>
      <div class="chart-background">
        <div id="main" class="chart"></div>
      </div>
    </div>

    <div class="vertical-line"></div>

    <div class="data-area">
      <h2 class="data-title">Data Overview</h2>
      <div v-if="infoData">
        <p><strong>Coin:</strong> {{ this.infoData[0].name }}</p>
        <p><strong>Market:</strong> {{ this.infoData[0].market }}</p>
        <p><strong>Max Price:</strong> {{ this.infoData[0].maxprice }} at {{ this.infoData[0].maxpricetime }}</p>
        <p><strong>Min Price:</strong> {{ this.infoData[0].minprice }} at {{ this.infoData[0].minpricetime }}</p>
        <hr class="data-divider"/>
      </div>
    </div>
  </div>
</template>

<style>

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  margin-bottom: 80px;
  padding: 0 20px;
}

.el-dropdown-link {
  display: flex;
  align-items: center;
}

.container {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  width: 100%;
  margin-top: 20px;
}

.chart-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-right: 20px;
}

.search-area {
  width: 100%;
  margin-bottom: 20px;
}

.panel {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.inline-elements {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  margin-bottom: 15px;
}

.inline-elements label {
  font-weight: bold;
  color: #333;
  margin-right: 10px;
}

.input {
  margin-right: 10px;
  flex: 1;
}

.date-picker {
  margin-top: 10px;
  width: 100%;
}

.button {
  background-color: #1890ff;
  color: #fff;
  width: 100px;
  align-self: flex-end;
  margin-top: 10px;
}

.chart-background {
  background-color: #f3efef;
  width: 900px;
  height: 500px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.chart {
  width: 100%;
  height: 100%;
}

.vertical-line {
  width: 2px;
  background-color: #000;
  height: 700px;
  margin-top: 0px;
}

.data-area {
  margin-top: 220px;
  width: 300px;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.data-title {
  font-size: 22px;
  font-weight: bold;
  color: #333;
  text-align: center;
  margin-bottom: 20px;
  text-transform: uppercase;
}

.data-content p {
  margin-bottom: 10px;
  font-size: 16px;
  color: #666;
}

.data-divider {
  border-top: 1px solid #d9d9d9;
  margin: 20px 0;
}
</style>

<script>
import * as echarts from 'echarts';
import request from '@/utils/request';

export default {
  data() {
    return {
      value1: [],
      coinname: '',
      market: '',
      infoData: null,
      chart: null,
      availableCoins: ['bitcoin', 'ethereum', 'binancecoin'],
    };
  },
  mounted() {
    this.chart = echarts.init(document.getElementById('main'));
  },
  methods: {

    querySearch(queryString, callback) {
      const results = queryString
          ? this.availableCoins.filter(coin => coin.toLowerCase().includes(queryString.toLowerCase()))
          : this.availableCoins;
      callback(results.map(coin => ({value: coin})));
    },

    search() {
      if (this.coinname.trim() === '' || this.market.trim() === '') {
        alert('Please enter a valid coin name or market.');
        return;
      }

      if (this.value1.length !== 2) {
        alert('Please select a valid date range');
        return;
      }

      if (this.market === "CoinGecko") {
        this.searchHistory_CoinGecko()
      } else if (this.market === "CoinMarketCap") {
        alert('Because the restriction of API, this service is not provided.')
        this.searchHistory_CoinMarketCap(this.coinname)
      }

    },

    async searchHistory_CoinMarketCap(name) {

      if (name === 'bitcoin') {
        this.name_temp = 'BTC';
      } else if (name === 'ethereum') {
        this.name_temp = 'ETH';
      } else if (name === 'binancecoin') {
        this.name_temp = 'BNB';
      }

      const [start, end] = this.value1.map(date => new Date(date).toISOString());
      const url = `https://pro-api.coinmarketcap.com/v2/cryptocurrency/quotes/historical?symbol=${this.name_temp}&time_start=${start}&time_end=${end}&interval=daily&convert=USD`;

      try {
        const response = await request.get(url, {
          headers: {
            'X-CMC_PRO_API_KEY': 'b13448ca-2c50-4c4a-91ee-d3c1e18dd314',
          },
        });

        const data = response.data.data.quotes;
        const dates = data.map(quote => new Date(quote.timestamp).toLocaleDateString());
        const values = data.map(quote => quote.quote.USD.price);

        this.updateChart(dates, values);
        this.updateInfoData(this.coinname, dates, values);
      } catch (error) {
        console.error(error);
        this.$message.error('Failed to fetch historical data');
      }
    },

    async searchHistory_CoinGecko() {

      const [start, end] = this.value1.map(date => Math.floor(date.getTime() / 1000)); // Transform date into Unix
      const url = `https://api.coingecko.com/api/v3/coins/${this.coinname}/market_chart/range?vs_currency=usd&from=${start}&to=${end}&precision=2`;

      try {
        const response = await request.get(url);
        const prices = response.prices;

        const dates = prices.map(price => new Date(price[0]).toLocaleDateString());
        const values = prices.map(price => price[1]);

        this.updateChart(dates, values);
        this.updateInfoData(this.coinname, dates, values);
      } catch (error) {
        console.error(error);
        this.$message.error('Failed to fetch historical data');
      }
    },

    updateChart(dates, values) {
      const minPrice = Math.min(...values);
      const maxPrice = Math.max(...values);
      const interval = 500;
      const min = Math.floor(minPrice / interval) * interval;
      const max = Math.ceil(maxPrice / interval) * interval;

      const option = {
        title: {
          text: 'Historical Price',
          left: '50%',
          textAlign: 'center'
        },

        xAxis: {
          name: 'time',
          type: 'category',
          data: dates,
        },
        yAxis: {
          name: 'Price',
          type: 'value',
          min: min,
          max: max,
          interval: interval,
        },
        tooltip: {
          trigger: 'axis',
          formatter: function (params) {
            if (params.length > 0) {
              let index = params[0].dataIndex;
              let date = params[0].name;
              let price = params[0].data;
              let coinName = this.coinname;
              let market = this.market;
              return `Coin: ${coinName}<br/>Market: ${market}<br/>Price: ${price}<br/>Time: ${date}`;
            }
            return '';
          }.bind(this),
          axisPointer: {
            animation: false
          }
        },
        series: [
          {
            data: values,
            type: 'line',
            smooth: true,
            lineStyle: {
              color: {
                type: 'linear',
                x: 0,
                y: 0,
                x2: 0,
                y2: 1,
                colorStops: [
                  {
                    offset: 0, color: 'rgb(255, 99, 71)'
                  },
                  {
                    offset: 1, color: 'rgba(255, 99, 71, 0.3)'
                  }
                ],
              },
              width: 2,
            },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                {offset: 0, color: 'rgba(255, 99, 71, 0.5)'},
                {offset: 1, color: 'rgba(255, 99, 71, 0)'}
              ])
            },
          },
        ],
      };
      this.chart.setOption(option);
    },

    updateInfoData(name, dates, values) {
      const maxPrice = Math.max(...values);
      const minPrice = Math.min(...values);
      const maxPriceIndex = values.indexOf(maxPrice);
      const minPriceIndex = values.indexOf(minPrice);

      this.infoData = [{
        name: name,
        market: 'USD',
        maxprice: maxPrice,
        maxpricetime: dates[maxPriceIndex],
        minprice: minPrice,
        minpricetime: dates[minPriceIndex],
      }];
    },
  },
};
</script>
