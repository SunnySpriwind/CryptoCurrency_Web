<template>
  <el-container>
    <el-aside width="200px">
      <el-menu :default-openeds="['1', '3']" @select="handleSelect">
        <el-submenu index="1" class="parent-item">
          <template slot="title"><i class="el-icon-message"></i>Charts</template>
          <el-menu-item-group>
            <template slot="title">Price Charts</template>
            <el-menu-item index="price-real-time">Real-time</el-menu-item>
            <el-menu-item index="price-historical">Historical</el-menu-item>
          </el-menu-item-group>
          <el-menu-item-group title="MarketCap Charts">
            <el-menu-item index="marketcap-real-time">Real-time</el-menu-item>
            <el-menu-item index="marketcap-historical">Historical</el-menu-item>
          </el-menu-item-group>
          <el-menu-item-group title="Volume Charts">
            <el-menu-item index="volume-real-time">Real-time</el-menu-item>
            <el-menu-item index="volume-historical">Historical</el-menu-item>
          </el-menu-item-group>
          <el-menu-item-group title="K-line Charts">
            <el-menu-item index="kline">K-line</el-menu-item>
          </el-menu-item-group>
        </el-submenu>
      </el-menu>
    </el-aside>

    <el-main>
      <div v-for="chart in selectedCharts" :key="chart" :style="chartStyle">
        <div ref="chart" :id="'chart-' + chart" :style="{ width: '100%', height: '100%' }"></div>
      </div>
    </el-main>
  </el-container>
</template>

<style>

.el-menu-item, .el-submenu__title {
  padding: 0 20px;
  font-size: 14px;
  font-weight: normal;
}

.el-menu-item-group__title {
  padding: 7px 0 7px 20px;
  line-height: normal;
  font-size: 16px;
  color: black;
  font-weight: bold;
}

.el-menu-item.is-active {
  background-color: transparent !important;
  color: black !important;
}
</style>

<script>
import request from '@/utils/request';
import * as echarts from 'echarts';
import {jwtDecode} from 'jwt-decode';

export default {
  name: 'Temp',
  data() {
    return {
      selectedCharts: [],
      defaultName:'bitcoin',
      defaultMarket:'CoinGecko',
      realtimePriceData: [],
      realtimeVolumeData: [],
      realtimeMarketcapData: [],
    };
  },

  computed: {
    chartStyle() {
      const count = this.selectedCharts.length;
      if (count === 1) {
        return {width: '100%', height: '100%'};
      } else if (count === 2) {
        return {width: '50%', height: '100%'};
      } else if (count >= 3) {
        return {width: '50%', height: '50%'};
      }
      return {};
    }
  },

  mounted() {
  },

  destroyed() {
  },

  methods: {

    async handleSelect(index) {
      const chartIndex = this.selectedCharts.indexOf(index);
      if (chartIndex === -1) {
        this.selectedCharts.push(index);
        console.log("add: " + this.selectedCharts);
      } else {
        this.selectedCharts.splice(chartIndex, 1);
        console.log("remove: " + this.selectedCharts);
      }
      this.$nextTick(() => {
        this.renderChart(index);
      });

      if (index === 'price-real-time' || index === 'volume-real-time' || index === 'marketcap-real-time') {
        await this.fetchRealTimeChartData();
      }

      this.$nextTick(() => {
        this.renderChart(index);
      });
    },

    async fetchRealTimeChartData() {
      try {
        const response = await request.get('/realtime/general', {
          params: { name: this.defaultName, market: this.defaultMarket }
        });

        if (response.code === 1) {

          console.log(JSON.stringify(response.data, null, 2));

          this.realtimePriceData = response.data.map(item => ({
            time: item.pricetime,
            price: parseFloat(item.price)
          }));

          this.realtimeVolumeData = response.data.map(item => ({
            time: item.pricetime,
            volume: parseFloat(item.vol)
          }));

          this.realtimeMarketcapData = response.data.map(item => ({
            time: item.pricetime,
            marketcap: item.marketcap ? parseFloat(item.marketcap) : null
          }));

          console.log('price: ', this.realtimePriceData);
          console.log('marketcap: ', this.realtimeMarketcapData);
          console.log('volume: ', this.realtimeVolumeData);
        } else {
          console.error('Fetch fails:', response.message);
        }
      } catch (error) {
        console.error('Request fails:', error);
      }
    },

    renderChart(chartType) {
      const chart = echarts.init(document.getElementById('chart-' + chartType));
      let option;

      if (chartType === 'price-real-time') {
        const times = this.realtimePriceData.map(item => item.pricetime);
        const prices = this.realtimePriceData.map(item => ({
          value: item.price,
          pricetime: item.pricetime,
          coinName: item.coinName,
          market: item.market
        }));

        const minValue = Math.min(...this.realtimePriceData.map(item => item.price));
        const maxValue = Math.max(...this.realtimePriceData.map(item => item.price));

        option = {
          title: {
            text: 'Realtime Chart',
            left: '50%',
            textAlign: 'center',
          },
          tooltip: {
            trigger: 'axis',
            formatter: function (params) {
              if (params.length > 0) {
                const data = params[0].data;
                let date = new Date(data.pricetime);
                let hours = date.getHours().toString().padStart(2, '0');
                let minutes = date.getMinutes().toString().padStart(2, '0');
                let coinName = data.coinName || 'Unknown';
                let market = data.market === '1' ? 'CoinGecko' : data.market === '3' ? 'CoinMarketCap' : 'Unknown';

                return `Coin: ${coinName}<br/>Market: ${market}<br/>Price: ${data.value}<br/>Time: ${hours}:${minutes}`;
              }
              return '';
            },
            axisPointer: {
              animation: false,
            },
          },
          xAxis: {
            type: 'category',
            data: times,
            splitLine: {
              show: true,
            },
          },
          yAxis: {
            type: 'value',
            min: minValue,
            max: maxValue,
          },
          dataZoom: [
            {
              type: 'slider',
              show: true,
              xAxisIndex: [0],
              start: 0,
              end: 100,
            },
            {
              type: 'inside',
              xAxisIndex: [0],
              start: 0,
              end: 100,
            }
          ],
          series: [
            {
              type: this.chartTypes[chart],
              data: prices,
              markLine: {
                data: [
                  {
                    yAxis: maxValue,
                    name: 'Resistance',
                    lineStyle: {
                      color: 'red',
                      type: 'dashed'
                    }
                  },
                  {
                    yAxis: minValue,
                    name: 'Support',
                    lineStyle: {
                      color: 'green',
                      type: 'dashed'
                    }
                  }
                ]
              },
              areaStyle: {
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                  {offset: 0, color: 'rgba(58,77,233,0.8)'},
                  {offset: 1, color: 'rgba(58,77,233,0.3)'}
                ])
              },
              lineStyle: {
                color: 'rgba(58,77,233,1)',
              },
            },
          ],
        };

      } else if (chartType === 'marketcap-real-time') {
        option = {
          title: {
            text: 'MarketCap Real-time Chart',
            left: 'center'
          },
          xAxis: {
            type: 'category',
            data: this.realtimeMarketcapData.map(item => item.time),
          },
          yAxis: {
            type: 'value',
            axisLabel: {
              formatter: function (value) {
                return (value / 1e9).toFixed(2) + 'B';
              }
            }
          },
          series: [{
            data: this.realtimeMarketcapData.map(item => item.marketcap),
            type: 'line',
            smooth: true
          }]
        };

      } else if (chartType === 'kline') {
        // 假设处理后的数据包含kline相关的数据
        option = {
          title: {
            text: 'Kline Chart',
            left: 'center'
          },
          xAxis: {
            type: 'category',
            data: this.realtimePriceData.map(item => item.time),
          },
          yAxis: {
            type: 'value'
          },
          series: [{
            data: this.realtimePriceData.map(item => [item.open, item.high, item.low, item.close]),  // K线图使用
            type: 'candlestick'
          }]
        };
      }

      chart.setOption(option);
    }
  }
};

</script>