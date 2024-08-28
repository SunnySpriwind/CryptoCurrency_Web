<template>
  <div class="container">
    <div class="chart-area">
      <div class="input-area">
        <div class="coin-group">
          <label class="form-label">Left Coin:</label>
          <el-autocomplete
              class="input"
              v-model="leftname"
              :fetch-suggestions="querySearchLeft"
              placeholder="Enter the name of coin"
          ></el-autocomplete>
          <el-select class="input" v-model="leftmarket" placeholder="Select Market for left axis">
            <el-option v-for="market in markets" :key="market" :label="market" :value="market"></el-option>
          </el-select>
        </div>

        <div class="coin-group">
          <label class="form-label">Right Coin:</label>
          <el-autocomplete
              class="input"
              v-model="rightname"
              :fetch-suggestions="querySearchRight"
              placeholder="Enter the name of coin"
          ></el-autocomplete>
          <el-select class="input" v-model="rightmarket" placeholder="Select Market for right axis">
            <el-option v-for="market in markets" :key="market" :label="market" :value="market"></el-option>
          </el-select>
        </div>

        <div class="button-group">
          <el-button class="button" @click="updateChart">Update</el-button>
          <el-button class="button" @click="resetInputs">Reset</el-button>
          <el-button class="button" @click="exportToExcel">Export</el-button>
        </div>
      </div>

      <div class="chart-background">
        <div id="main" class="chart"></div>
      </div>

      <div class="change-chart">
        <el-button class="button" @click="toggleChart('price')">Price</el-button>
        <el-button class="button" @click="toggleChart('exchange')" style="width:100px">Exchange</el-button>
      </div>
    </div>

    <div class="vertical-line"></div>

    <div class="data-area">
      <h2 class="data-title">24-Hour Data Overview</h2>
      <div v-if="currentChart === 'price' && infoData" class="data-content">
        <p><strong>Coin:</strong> {{ this.leftname }}</p>
        <p><strong>Max Price:</strong> {{ infoData[0].maxprice }} (USD) at {{ infoData[0].maxpricetime }}</p>
        <p><strong>Min Price:</strong> {{ infoData[0].minprice }} (USD) at {{ infoData[0].minpricetime }}</p>
        <p><strong>Market:</strong> {{ this.leftmarket }}</p>
      </div>
      <hr class="data-divider"/>
      <div v-if="currentChart === 'price' && infoDataAnother" class="data-content">
        <p><strong>Coin:</strong> {{ this.rightname }}</p>
        <p><strong>Max Price:</strong> {{ infoDataAnother[0].maxprice }} (USD) at {{ infoDataAnother[0].maxpricetime }}
        </p>
        <p><strong>Min Price:</strong> {{ infoDataAnother[0].minprice }} (USD) at {{ infoDataAnother[0].minpricetime }}
        </p>
        <p><strong>Market:</strong> {{ this.rightmarket }}</p>
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

.input-area {
  width: 800px;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  margin-bottom: 20px;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.coin-group {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  width: 100%;
}

.form-label {
  width: 120px;
  text-align: right;
  margin-right: 10px;
  font-weight: bold;
  font-size: 16px;
}

.input {
  flex: 1;
  margin-right: 20px;
}

.button-group {
  display: flex;
  justify-content: flex-start;
  width: 100%;
}

.button {
  width: 100px;
  margin-left: 220px;
  padding: 10px 20px;
  background-color: #1890ff;
  color: #fff;
  border-radius: 4px;
  border: none;
  cursor: pointer;
  transition: background-color 0.3s;
}

.button:hover {
  background-color: #40a9ff;
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

.change-chart {
  margin-left: -170px;
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.vertical-line {
  width: 2px;
  background-color: #000;
  height: 700px;
  margin-top: 0px;
}

.data-area {
  margin-top: 60px;
  width: 350px;
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
import * as XLSX from 'xlsx';

export default {
  name: 'ComparingView',

  data() {
    return {
      leftname: '',
      beforeleftname: '',
      rightname: '',
      beforerightname: '',
      leftmarket: '',
      beforeleftmarket: '',
      rightmarket: '',
      beforerightmarket: '',
      availableCoins: ['bitcoin', 'ethereum', 'binancecoin'],
      markets: ['CoinGecko', 'CoinMarketCap'],
      chart: null,
      tooltipData: null,
      infoData: null,
      infoDataAnother: null,
      exchangeInfoData: null,
      currentChart: 'price',

      // Chart's title
      option: {
        title: {
          text: 'Real-Time Price in 1 Day',
          left: '50%',
          textAlign: 'center'
        },

        // Chart's points and x,y-axis
        tooltip: {
          trigger: 'axis',
          formatter: function (params) {
            if (params.length > 0) {
              let tooltipHtml = '';
              params.forEach(param => {
                let date = new Date(param.value[0]);
                let hours = date.getHours().toString().padStart(2, '0');
                let minutes = date.getMinutes().toString().padStart(2, '0');
                let coinName = param.seriesName.replace(' Price', '');
                let market = param.value[2];
                market = market === '1' ? 'CoinGecko' : market === '3' ? 'CoinMarketCap' : market;
                tooltipHtml += `Coin: ${coinName}<br/>Market: ${market}<br/>Price: ${param.value[1]}<br/>Time: ${hours}:${minutes}<br/>`;
              });
              return tooltipHtml;
            }
            return '';
          },

          axisPointer: {
            animation: false
          }
        },

        xAxis: {
          type: 'time',
          splitLine: {
            show: false
          },
          axisLabel: {
            formatter: function (value) {
              let date = new Date(value);
              let hours = date.getHours().toString().padStart(2, '0');
              let minutes = date.getMinutes().toString().padStart(2, '0');
              return `${hours}:${minutes}`;
            }
          },
          minInterval: 300000,
        },

        yAxis: [
          {
            type: 'value',
            name: 'Left Axis',
            position: 'left',
            boundaryGap: [0, '50%'],
            splitLine: {
              show: true
            },
            axisLabel: {
              formatter: '{value} $'
            }
          },
          {
            type: 'value',
            name: 'Right Axis',
            position: 'right',
            boundaryGap: [0, '50%'],
            splitLine: {
              show: false
            },
            axisLabel: {
              formatter: '{value} $'
            }
          }
        ],
        series: []
      }
    };
  },

  mounted() {
    // Initialize chart
    this.chart = echarts.init(document.getElementById('main'));
    this.chart.setOption(this.option);
  },

  beforeDestroy() {
    // Clear the interval when the component is destroyed
    if (this.intervalId) {
      clearInterval(this.intervalId);
    }
  },

  methods: {

    querySearchLeft(queryString, callback) {
      const results = queryString
          ? this.availableCoins.filter(coin => coin.toLowerCase().includes(queryString.toLowerCase()))
          : this.availableCoins;
      callback(results.map(coin => ({value: coin})));
    },
    querySearchRight(queryString, callback) {
      const results = queryString
          ? this.availableCoins.filter(coin => coin.toLowerCase().includes(queryString.toLowerCase()))
          : this.availableCoins;
      callback(results.map(coin => ({value: coin})));
    },

    // Update chart based on selected currencies.
    updateChart() {

      // Clear existing series.
      this.option.series = [];

      // Check inputs is null or not.
      const leftname = this.leftname.trim() !== '' ? this.leftname.trim() : this.beforeLeftname;
      const leftmarket = this.leftmarket.trim() !== '' ? this.leftmarket.trim() : this.beforeLeftmarket;
      const rightname = this.rightname.trim() !== '' ? this.rightname.trim() : this.beforeRightname;
      const rightmarket = this.rightmarket.trim() !== '' ? this.rightmarket.trim() : this.beforeRightmarket;

      this.getStaticDataForLeftCoin(leftname, leftmarket);
      this.getStaticDataForRightCoin(rightname, rightmarket);

      // Request.
      if (leftname && leftmarket) {
        request.get('/realtime/general', {params: {name: leftname, market: leftmarket}})
            .then(response => {
              if (response.code === 1) {
                this.processData(response.data, `${leftname} Price`, leftmarket, 0);
                this.beforeLeftname = leftname;
                this.beforeLeftmarket = leftmarket;
              } else {
                console.error('Error fetching data:', response.message);
              }
            })
            .catch(error => {
              console.error('Error fetching data:', error);
            });
      }

      if (rightname && rightmarket) {
        request.get('/realtime/general', {params: {name: rightname, market: rightmarket}})
            .then(response => {
              if (response.code === 1) {
                this.processData(response.data, `${rightname} Price`, rightmarket, 1);
                if (!this.intervalId) {
                  this.startTimer();
                }
                this.beforeRightname = rightname;
                this.beforeRightmarket = rightmarket;
              } else {
                console.error('Error fetching data:', response.message);
              }
            })
            .catch(error => {
              console.error('Error fetching data:', error);
            });
      }
    },

    // Process data and add series
    processData(data, seriesName, market, yAxisIndex) {

      // Reset x, yAxis
      if (!this.option.yAxis[yAxisIndex]) {
        this.option.yAxis[yAxisIndex] = {
          type: 'value',
          name: '',
          position: yAxisIndex === 0 ? 'left' : 'right',
          boundaryGap: [0, '50%'],
          splitLine: {
            show: yAxisIndex === 0
          },
          axisLabel: {
            formatter: '{value} $'
          }
        };
      }

      this.option.xAxis = {
        type: 'time',
        splitLine: {
          show: false
        },
        axisLabel: {
          formatter: function (value) {
            let date = new Date(value);
            let hours = date.getHours().toString().padStart(2, '0');
            let minutes = date.getMinutes().toString().padStart(2, '0');
            return `${hours}:${minutes}`;
          }
        },
        minInterval: 300000,
      };

      this.option.yAxis[yAxisIndex].name = seriesName + " (" + market + ")";

      // Select the range of time.
      let now = new Date();
      let twentyFourHoursAgo = new Date(now - 2 * 60 * 60 * 1000);
      data = data.filter(item => new Date(item.pricetime) >= twentyFourHoursAgo);

      // Sort data.
      data.sort((a, b) => new Date(a.pricetime) - new Date(b.pricetime));

      let filteredData = [];
      let lastPrice = null;
      data.forEach(item => {
        let currentPrice = parseFloat(item.price);
        if (currentPrice !== lastPrice) {
          filteredData.push(item);
          lastPrice = currentPrice;
        }
      });

      let formattedData = filteredData.map(item => ({
        name: new Date(item.pricetime).getTime(),
        value: [
          new Date(item.pricetime).getTime(),
          parseFloat(item.price),
          item.market
        ]
      }));

      let colorStart, colorEnd;
      if (yAxisIndex === 0) {
        colorStart = 'rgb(255, 99, 71)';
        colorEnd = 'rgba(255, 99, 71, 0.3)';
      } else {
        colorStart = 'rgb(54, 162, 235)';
        colorEnd = 'rgba(54, 162, 235, 0.3)';
      }

      // Create chart.
      let newSeries = {
        name: `${seriesName} (${market})`,
        type: 'line',
        showSymbol: true,
        symbolSize: 5,
        data: formattedData,
        large: true,
        yAxisIndex: yAxisIndex,
        lineStyle: {
          width: 2,
          color: colorStart
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            {offset: 0, color: colorStart},
            {offset: 1, color: colorEnd}
          ])
        }
      };

      const existingSeriesIndex = this.option.series.findIndex(series => series.name === newSeries.name);
      if (existingSeriesIndex !== -1) {
        this.option.series[existingSeriesIndex].data = newSeries.data;
      } else {
        this.option.series.push(newSeries);
      }

      this.updateYAxisRange();
      this.chart.setOption(this.option);
    },

    // Update Y-axis range
    updateYAxisRange() {

      let leftPrices = [];
      let rightPrices = [];

      this.option.series.forEach(series => {
        if (series.yAxisIndex === 0) {
          leftPrices = leftPrices.concat(series.data.map(item => item.value[1]));
        } else if (series.yAxisIndex === 1) {
          rightPrices = rightPrices.concat(series.data.map(item => item.value[1]));
        }
      });

      if (leftPrices.length > 0) {
        let minLeftPrice = Math.min(...leftPrices);
        let maxLeftPrice = Math.max(...leftPrices);
        this.option.yAxis[0].min = Math.floor(minLeftPrice / 500) * 500;
        this.option.yAxis[0].max = Math.ceil(maxLeftPrice / 500) * 500;
      } else {
        this.option.yAxis[0].min = null;
        this.option.yAxis[0].max = null;
      }

      if (rightPrices.length > 0) {
        let minRightPrice = Math.min(...rightPrices);
        let maxRightPrice = Math.max(...rightPrices);
        this.option.yAxis[1].min = Math.floor(minRightPrice / 50) * 50;
        this.option.yAxis[1].max = Math.ceil(maxRightPrice / 50) * 50;
      } else {
        this.option.yAxis[1].min = null;
        this.option.yAxis[1].max = null;
      }
    },

    getStaticDataForLeftCoin(name, market) {
      this.infoData = [];

      // Initialize infoData with an object
      let infoDataObj = {
        id: 0,
        name: name,
        maxprice: null,
        minprice: null,
        market: market,
        maxpricetime: null,
        minpricetime: null,
        vol: null
      };

      this.infoData.push(infoDataObj);

      request.get('/realtime/maxprice', {params: {name: name, market: market}})
          .then(response => {
            if (response.code === 1) {
              let {price, pricetime} = response.data;
              this.infoData[0].maxprice = price;  // Use index to update the first object's property
              this.infoData[0].maxpricetime = pricetime;

            } else {
              console.error('Error Static data:', response.message);
            }
          })
          .catch(error => {
            console.error('Error Static data:', error);
          });

      request.get('/realtime/minprice', {params: {name: name, market: market}})
          .then(response => {
            if (response.code === 1) {
              let {price, pricetime} = response.data;
              this.infoData[0].minprice = price;  // Use index to update the first object's property
              this.infoData[0].minpricetime = pricetime;

            } else {
              console.error('Error Static data:', response.message);
            }
          })
          .catch(error => {
            console.error('Error Static data:', error);
          });
    },

    getStaticDataForRightCoin(name, market) {
      this.infoDataAnother = [];

      // Initialize infoData with an object
      let infoDataObj = {
        id: 0,
        name: name,
        maxprice: null,
        minprice: null,
        market: market,
        maxpricetime: null,
        minpricetime: null,
        vol: null
      };

      this.infoDataAnother.push(infoDataObj);

      request.get('/realtime/maxprice', {params: {name: name, market: market}})
          .then(response => {
            if (response.code === 1) {
              let {price, pricetime} = response.data;
              this.infoDataAnother[0].maxprice = price;  // Use index to update the first object's property
              this.infoDataAnother[0].maxpricetime = pricetime;

            } else {
              console.error('Error Static data:', response.message);
            }
          })
          .catch(error => {
            console.error('Error Static data:', error);
          });

      request.get('/realtime/minprice', {params: {name: name, market: market}})
          .then(response => {
            if (response.code === 1) {
              let {price, pricetime} = response.data;
              this.infoDataAnother[0].minprice = price;  // Use index to update the first object's property
              this.infoDataAnother[0].minpricetime = pricetime;

            } else {
              console.error('Error Static data:', response.message);
            }
          })
          .catch(error => {
            console.error('Error Static data:', error);
          });
    },

    updateExchangeChart() {
      // Clear existing series and yAxis
      this.option.series = [];
      this.chart.clear();

      // Generate the new yAxis
      this.option.yAxis = [{
        type: 'value',
        name: 'Exchange Rate',
        position: 'left',
        boundaryGap: [0, '50%'],
        splitLine: {
          show: true
        },
        axisLabel: {
          formatter: '{value}'
        }
      }];

      const leftname = this.leftname.trim() !== '' ? this.leftname.trim() : this.beforeleftname;
      const leftmarket = this.leftmarket.trim() !== '' ? this.leftmarket.trim() : this.beforeleftmarket;
      const rightname = this.rightname.trim() !== '' ? this.rightname.trim() : this.beforerightname;
      const rightmarket = this.rightmarket.trim() !== '' ? this.rightmarket.trim() : this.beforerightmarket;

      if (leftname && rightname && leftmarket && rightmarket) {
        Promise.all([
          request.get('/realtime/general', {params: {name: leftname, market: leftmarket}}),
          request.get('/realtime/general', {params: {name: rightname, market: rightmarket}})
        ])
            .then(responses => {
              const leftData = responses[0].data;
              const rightData = responses[1].data;

              const now = new Date();
              const twoHoursAgo = new Date(now - 2 * 60 * 60 * 1000);

              const validDataLength = Math.min(leftData.length, rightData.length);
              const validLeftData = leftData.slice(0, validDataLength).filter(item => item && item.hasOwnProperty('price'));
              const validRightData = rightData.slice(0, validDataLength).filter(item => item && item.hasOwnProperty('price'));

              let exchangeData = validLeftData.map((leftItem, index) => {
                const rightItem = validRightData[index];
                if (leftItem && rightItem && leftItem.hasOwnProperty('price') && rightItem.hasOwnProperty('price')) {
                  const exchangeRate = parseFloat(leftItem.price) / parseFloat(rightItem.price);
                  return {
                    pricetime: leftItem.pricetime,
                    exchangeRate: exchangeRate,
                  };
                }
                return null;
              }).filter(item => item && new Date(item.pricetime) >= twoHoursAgo);

              let filteredData = [];
              let lastExchangeRate = null;
              exchangeData.forEach(item => {
                if (item.exchangeRate !== lastExchangeRate) {
                  filteredData.push(item);
                  lastExchangeRate = item.exchangeRate;
                }
              });

              this.option.series = [{
                name: 'Exchange Rate',
                type: 'line',
                showSymbol: true,
                symbolSize: 5,
                data: filteredData.map(item => ({
                  name: new Date(item.pricetime).getTime(),
                  value: [
                    new Date(item.pricetime).getTime(),
                    item.exchangeRate,
                    `${leftname}/${rightname}`
                  ]
                })),
                large: true,
                lineStyle: {
                  width: 2,
                  color: 'rgb(75, 192, 192)'
                },
                areaStyle: {
                  color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                    {offset: 0, color: 'rgba(75, 192, 192, 0.5)'},
                    {offset: 1, color: 'rgba(75, 192, 192, 0)'}
                  ])
                }
              }];

              this.option.xAxis = {
                type: 'time',
                splitLine: {
                  show: false
                },
                axisLabel: {
                  formatter: function (value) {
                    let date = new Date(value);
                    let hours = date.getHours().toString().padStart(2, '0');
                    let minutes = date.getMinutes().toString().padStart(2, '0');
                    return `${hours}:${minutes}`;
                  }
                },
                min: twoHoursAgo.getTime(),
                max: now.getTime()
              };

              let exchangeRates = filteredData.map(item => item.exchangeRate);
              if (exchangeRates.length > 0) {
                let minExchangeRate = Math.min(...exchangeRates);
                let maxExchangeRate = Math.max(...exchangeRates);
                this.option.yAxis[0].min = Math.floor(minExchangeRate / 0.01) * 0.01;
                this.option.yAxis[0].max = Math.ceil(maxExchangeRate / 0.01) * 0.01;
              } else {
                this.option.yAxis[0].min = null;
                this.option.yAxis[0].max = null;
              }

              this.option.tooltip = {
                trigger: 'axis',
                formatter: function (params) {
                  if (params.length > 0) {
                    let date = new Date(params[0].value[0]);
                    let hours = date.getHours().toString().padStart(2, '0');
                    let minutes = date.getMinutes().toString().padStart(2, '0');
                    return `Coin: ${leftname}/${rightname}<br/>Market: ${leftmarket}/${rightmarket}<br/>Exchange Rate: ${params[0].value[1]}<br/>Time: ${hours}:${minutes}`;
                  }
                  return '';
                },
                axisPointer: {
                  animation: false
                }
              };

              this.chart.setOption(this.option);
            })
            .catch(error => {
              console.error('Error fetching data:', error);
            });
      }
    },

    toggleChart(chartType) {

      this.currentChart = chartType;

      this.updateTooltip();
      // Immediately update the chart
      this.update();
    },

    update() {

      if (this.currentChart === 'price') {
        this.updateChart();
      } else if (this.currentChart === 'exchange') {
        this.updateExchangeChart();
      }
      this.startTimer();
    },

    startTimer() {
      // Clear any existing timer to avoid multiple timers running simultaneously
      if (this.intervalId) {
        clearInterval(this.intervalId);
      }

      // Set a new interval based on the current chart type
      if (this.currentChart === 'price') {
        this.intervalId = setInterval(this.updateChart, 60 * 1000);
      } else if (this.currentChart === 'exchange') {
        this.intervalId = setInterval(this.updateExchangeChart, 60 * 1000);
      }
    },

    updateTooltip() {
      if (this.currentChart === 'price') {

        this.option.xAxis = {
          type: 'time',
          splitLine: {
            show: false
          },
          axisLabel: {
            formatter: function (value) {
              let date = new Date(value);
              let hours = date.getHours().toString().padStart(2, '0');
              let minutes = date.getMinutes().toString().padStart(2, '0');
              return `${hours}:${minutes}`;
            }
          },
          minInterval: 300000,
        }

        this.option.yAxis = [
          {
            type: 'value',
            name: 'Left Axis',
            position: 'left',
            boundaryGap: [0, '50%'],
            splitLine: {
              show: true
            },
            axisLabel: {
              formatter: '{value} $'
            }
          },
          {
            type: 'value',
            name: 'Right Axis',
            position: 'right',
            boundaryGap: [0, '50%'],
            splitLine: {
              show: false
            },
            axisLabel: {
              formatter: '{value} $'
            }
          }
        ]

        this.option.tooltip = {
          trigger: 'axis',
          formatter: function (params) {
            if (params.length > 0) {
              let tooltipHtml = '';
              params.forEach(param => {
                let date = new Date(param.value[0]);
                let hours = date.getHours().toString().padStart(2, '0');
                let minutes = date.getMinutes().toString().padStart(2, '0');
                let coinName = param.seriesName.replace(' Price', '');
                let market = param.value[2];
                market = market === '1' ? 'CoinGecko' : market === '3' ? 'CoinMarketCap' : market;
                tooltipHtml += `Coin: ${coinName}<br/>Market: ${market}<br/>Price: ${param.value[1]}<br/>Time: ${hours}:${minutes}<br/>`;
              });
              return tooltipHtml;
            }
            return '';
          },

          axisPointer: {
            animation: false
          }
        };
      } else if (this.currentChart === 'exchange') {
        this.option.tooltip = {
          trigger: 'axis',
          formatter: function (params) {
            if (params.length > 0) {
              let date = new Date(params[0].value[0]);
              let hours = date.getHours().toString().padStart(2, '0');
              let minutes = date.getMinutes().toString().padStart(2, '0');
              return `Coin: ${params[0].value[2]}<br/>Exchange Rate: ${params[0].value[1]}<br/>Time: ${hours}:${minutes}`;
            }
            return '';
          },
          axisPointer: {
            animation: false
          }
        };
      }
      this.chart.setOption(this.option);
    },

    exportToExcel() {
      if (!this.chart) return;

      const seriesData = this.option.series.map(series => {
        return series.data.map(item => {
          const marketLabel = item.value[2] === '1' ? 'CoinGecko' :
              item.value[2] === '3' ? 'CoinMarketCap' :
                  'Unknown Market';

          return {
            Time: new Date(item.value[0]).toLocaleString(),
            Value: item.value[1],
            Market: marketLabel,
            Coin: series.name
          };
        });
      }).flat();


      const worksheet = XLSX.utils.json_to_sheet(seriesData);


      const workbook = XLSX.utils.book_new();
      XLSX.utils.book_append_sheet(workbook, worksheet, 'Chart Data');


      const filename = `${this.leftname}_${this.rightname}_${this.currentChart}_data.xlsx`;
      XLSX.writeFile(workbook, filename);
    },

    resetInputs() {
      this.leftname = '';
      this.rightname = '';
      this.leftmarket = '';
      this.rightmarket = '';
      this.option.series = [];
      this.chart.setOption(this.option);
    },

  }
};

</script>




