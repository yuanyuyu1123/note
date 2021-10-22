import request from '@/utils/request'
import {praseStrEmpty} from "@/utils/ruoyi";

// 查询笔记列表
export function listNote(query) {
  return request({
    url: '/biz/note/list',
    method: 'get',
    params: query
  })
}

// 查询自己的笔记列表
export function listSelfNote() {
  return request({
    url: '/biz/note/listSelfNote',
    method: 'get',
  })
}

// 查询笔记详细
export function getNote(id) {
  return request({
    url: '/biz/note/' + id,
    method: 'get'
  })
}

// 查询用户详细
export function getUser() {
  return request({
    url: '/biz/note/user',
    method: 'get'
  })
}

// 新增笔记
export function addNote(data) {
  return request({
    url: '/biz/note',
    method: 'post',
    data: data
  })
}

// 修改笔记
export function updateNote(data) {
  return request({
    url: '/biz/note',
    method: 'put',
    data: data
  })
}

// 删除笔记
export function delNote(id) {
  return request({
    url: '/biz/note/' + id,
    method: 'delete'
  })
}

// 导出笔记
export function exportNote(query) {
  return request({
    url: '/biz/note/export',
    method: 'get',
    params: query
  })
}
