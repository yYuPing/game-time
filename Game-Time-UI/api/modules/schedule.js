import request from "../request";

/**
 * 获取某周的排课数据
 * @param {string} weekStart 周一起始日期，格式 YYYY-MM-DD
 * @returns {Promise}
 */
export function getWeekSchedule(weekStart) {
  return request({
    url: "/api/schedule",
    method: "GET",
    params: { weekStart }
  });
}

/**
 * 创建预约
 * @param {Object} data 预约数据 { date, timeslot, gameId, gameName, note }
 * @returns {Promise}
 */
export function createReservation(data) {
  return request({
    url: "/api/reservations",
    method: "POST",
    data
  });
}

/**
 * 删除某条预约
 * @param {number|string} id 预约 ID
 * @returns {Promise}
 */
export function deleteReservation(id) {
  return request({
    url: `/api/reservations/${id}`,
    method: "DELETE"
  });
}
