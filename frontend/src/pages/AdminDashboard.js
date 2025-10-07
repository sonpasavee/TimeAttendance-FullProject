import React, { useEffect, useState } from "react";
import API from "../api/api";
import Navbar from "../components/Navbar";
import {
  FaUserAlt,
  FaUserCheck,
  FaUserTimes,
  FaRegClock,
  FaCalendarTimes,
  FaCalendarCheck,
  FaBan,
  FaEdit,
  FaSave,
  FaTrash,
} from "react-icons/fa";

export default function AdminDashboard() {
  const [users, setUsers] = useState([]);
  const [leaves, setLeaves] = useState([]);
  const [attendance, setAttendance] = useState([]);
  const [editingUser, setEditingUser] = useState(null);
  const [rejectedCount, setRejectedCount] = useState(0);

  useEffect(() => {
    fetchUsers();
    fetchLeaves();
    fetchAttendance();
  }, []);

  const fetchUsers = async () => {
    try {
      const res = await API.get("/admin/users");
      setUsers(res.data);
    } catch (err) {
      console.error(err);
      alert("Failed to fetch users");
    }
  };

  const fetchLeaves = async () => {
    try {
      const res = await API.get("/admin/leave/pending");
      setLeaves(res.data);
    } catch (err) {
      console.error(err);
      alert("Failed to fetch leave requests");
    }
  };

  const fetchAttendance = async () => {
    try {
      const res = await API.get("/admin/attendance/all");
      setAttendance(res.data);
    } catch (err) {
      console.error(err);
      alert("Failed to fetch attendance records");
    }
  };

  // Approve Leave
  const approve = async (id) => {
    try {
      await API.put(`/admin/leave/${id}/approve`);
      const approvedLeave = leaves.find((l) => l.id === id);
      if (approvedLeave) {
        const newAttendance = {
          id: `leave-${id}`,
          username: approvedLeave.username,
          date: approvedLeave.startDate,
          clockIn: null,
          clockOut: null,
          status: "Leave",
          method: "-",
          location: "-",
        };
        setAttendance((prev) => [...prev, newAttendance]);
      }
      setLeaves((prev) => prev.filter((l) => l.id !== id));
      alert("✅ Leave approved and added to attendance!");
    } catch (err) {
      console.error(err);
      alert("Failed to approve leave");
    }
  };

  // Reject Leave
  const reject = async (id) => {
    try {
      await API.put(`/admin/leave/${id}/reject`);
      setLeaves((prev) => prev.filter((l) => l.id !== id));
      setRejectedCount((prev) => prev + 1);
      alert("❌ Leave rejected!");
    } catch (err) {
      console.error(err);
      alert("Failed to reject leave");
    }
  };

  const startEdit = (user) => setEditingUser({ ...user });

  const saveEdit = async () => {
    if (!editingUser || !editingUser.username.trim()) return;
    try {
      await API.put(`/admin/users/${editingUser.id}`, {
        username: editingUser.username,
      });
      setEditingUser(null);
      fetchUsers();
      alert("✅ User updated successfully");
    } catch (err) {
      console.error(err);
      alert("Failed to update user");
    }
  };

  const deleteUser = async (id) => {
    if (!window.confirm("Are you sure you want to delete this user?")) return;
    try {
      await API.delete(`/admin/users/${id}`);
      fetchUsers();
      alert("🗑️ User deleted successfully");
    } catch (err) {
      console.error(err);
      alert("Failed to delete user");
    }
  };

  const formatDate = (dateValue) => {
    if (!dateValue) return "-";
    const d = new Date(dateValue);
    return isNaN(d.getTime()) ? "-" : d.toLocaleDateString();
  };

  const formatDateTime = (dateValue) => {
    if (!dateValue) return "-";
    const d = new Date(dateValue);
    return isNaN(d.getTime()) ? "-" : d.toLocaleString();
  };

  const summary = {
    totalClockIn: attendance.filter((a) => a.clockIn).length,
    totalClockOut: attendance.filter((a) => a.clockOut).length,
    
    totalLeave: attendance.filter((a) => a.status === "Leave").length,
  };

  const normalUsers = users.filter(
    (u) => u.role && u.role.toLowerCase() !== "admin"
  );
  const totalNormalUsers = normalUsers.length;

  return (
    <div style={{ backgroundColor: "#f8f9fa", minHeight: "100vh" }}>
      <Navbar />
      <div className="container py-5">
        <h2 className="mb-4 text-center fw-bold text-primary">
          <FaUserAlt className="me-2" />
          Admin Dashboard
        </h2>

        {/* ✅ Summary Cards */}
        <div className="row g-4 mb-5">
          {[
            {
              title: "Total Users",
              value: totalNormalUsers,
              icon: <FaUserAlt />,
              color: "primary",
            },
            {
              title: "Clock In",
              value: summary.totalClockIn,
              icon: <FaRegClock />,
              color: "success",
            },
            {
              title: "Clock Out",
              value: summary.totalClockOut,
              icon: <FaUserCheck />,
              color: "info",
            },
            {
              title: "Leave",
              value: summary.totalLeave,
              icon: <FaCalendarCheck />,
              color: "secondary",
            },
            {
              title: "Rejected Leave",
              value: rejectedCount,
              icon: <FaBan />,
              color: "danger",
            },
          ].map((card, i) => (
            <div key={i} className="col-md-4 col-lg-2 col-6">
              <div
                className={`card text-center shadow border-0 bg-${card.color} bg-opacity-10`}
              >
                <div className="card-body">
                  <div
                    className={`text-${card.color} fs-2 mb-2`}
                    style={{ lineHeight: "1" }}
                  >
                    {card.icon}
                  </div>
                  <h6 className="fw-semibold">{card.title}</h6>
                  <h3 className="fw-bold">{card.value}</h3>
                </div>
              </div>
            </div>
          ))}
        </div>

        {/* Pending Leave Requests */}
        <div className="card shadow-lg mb-5 border-0">
          <div className="card-header bg-gradient bg-primary text-white fw-bold">
            Pending Leave Requests
          </div>
          <div className="card-body p-0">
            <table className="table table-hover align-middle mb-0">
              <thead className="table-light text-center">
                <tr>
                  <th>User</th>
                  <th>Reason</th>
                  <th>Start</th>
                  <th>End</th>
                  <th>Requested At</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody className="text-center">
                {leaves.length ? (
                  leaves.map((l) => (
                    <tr key={l.id}>
                      <td>{l.username}</td>
                      <td>{l.reason}</td>
                      <td>{formatDate(l.startDate)}</td>
                      <td>{formatDate(l.endDate)}</td>
                      <td>{formatDateTime(l.createdAt)}</td>
                      <td>
                        <button
                          className="btn btn-success btn-sm me-2"
                          onClick={() => approve(l.id)}
                        >
                          <FaCalendarCheck /> Approve
                        </button>
                        <button
                          className="btn btn-danger btn-sm"
                          onClick={() => reject(l.id)}
                        >
                          <FaCalendarTimes /> Reject
                        </button>
                      </td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td colSpan="6" className="py-3 text-muted">
                      No pending requests
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>

        {/* All Users */}
        <div className="card shadow-lg mb-5 border-0">
          <div className="card-header bg-gradient bg-secondary text-white fw-bold">
            All Users
          </div>
          <ul className="list-group list-group-flush">
            {users.length ? (
              users.map((u) => (
                <li
                  key={u.id}
                  className="list-group-item d-flex justify-content-between align-items-center"
                >
                  {editingUser?.id === u.id ? (
                    <input
                      className="form-control me-2"
                      value={editingUser.username}
                      onChange={(e) =>
                        setEditingUser({
                          ...editingUser,
                          username: e.target.value,
                        })
                      }
                    />
                  ) : (
                    <span>
                      <strong>{u.username}</strong> <small>({u.role})</small>
                    </span>
                  )}

                  <div>
                    {editingUser?.id === u.id ? (
                      <button
                        className="btn btn-success btn-sm me-2"
                        onClick={saveEdit}
                      >
                        <FaSave /> Save
                      </button>
                    ) : (
                      <button
                        className="btn btn-primary btn-sm me-2"
                        onClick={() => startEdit(u)}
                      >
                        <FaEdit /> Edit
                      </button>
                    )}
                    <button
                      className="btn btn-danger btn-sm"
                      onClick={() => deleteUser(u.id)}
                    >
                      <FaTrash /> Delete
                    </button>
                  </div>
                </li>
              ))
            ) : (
              <li className="list-group-item text-center text-muted">
                No users found
              </li>
            )}
          </ul>
        </div>

        {/* Attendance Records */}
        <div className="card shadow-lg border-0">
          <div className="card-header bg-gradient bg-info text-white fw-bold">
            Attendance Records
          </div>
          <div className="card-body p-0">
            <table className="table table-striped table-hover align-middle mb-0">
              <thead className="table-light text-center">
                <tr>
                  <th>User</th>
                  <th>Date</th>
                  <th>Clock In</th>
                  <th>Clock Out</th>
                  <th>Status</th>
                  <th>Method</th>
                  <th>Location</th>
                </tr>
              </thead>
              <tbody className="text-center">
                {attendance.length ? (
                  attendance.map((a) => (
                    <tr key={a.id}>
                      <td>{a.username}</td>
                      <td>{formatDate(a.date)}</td>
                      <td>{formatDateTime(a.clockIn)}</td>
                      <td>{formatDateTime(a.clockOut)}</td>
                      <td>
                        <span
                          className={`badge rounded-pill ${
                            a.status === "Present"
                              ? "bg-success"
                              : a.status === "Late"
                              ? "bg-warning text-dark"
                              : a.status === "Leave"
                              ? "bg-secondary"
                              : "bg-danger"
                          }`}
                        >
                          {a.status}
                        </span>
                      </td>
                      <td>{a.method}</td>
                      <td>{a.location}</td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td colSpan="7" className="py-3 text-muted">
                      No attendance records
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
}
