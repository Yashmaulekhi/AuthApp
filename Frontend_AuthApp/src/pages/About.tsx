function About() {
  return (
    <div className="min-h-screen bg-slate-950 text-white px-6 py-12">
      <div className="max-w-5xl mx-auto">
        <h1 className="text-5xl font-bold mb-6 text-cyan-400">
          About AuthApp
        </h1>

        <p className="text-lg text-gray-300 leading-8 mb-8">
          AuthApp is a modern authentication platform built to demonstrate secure,
          scalable, and production-ready user authentication. The application
          follows industry best practices for authentication and authorization,
          ensuring both security and an excellent user experience.
        </p>

        <div className="grid md:grid-cols-2 gap-8">
          <div className="bg-slate-900 p-6 rounded-xl border border-slate-800">
            <h2 className="text-2xl font-semibold text-cyan-400 mb-4">
              🚀 Features
            </h2>

            <ul className="space-y-3 text-gray-300">
              <li>✔ Secure User Registration & Login</li>
              <li>✔ JWT Authentication</li>
              <li>✔ Refresh Token Rotation</li>
              <li>✔ HTTP-Only Secure Cookies</li>
              <li>✔ Role-Based Authorization</li>
              <li>✔ Protected Routes</li>
              <li>✔ Persistent User Sessions</li>
              <li>✔ Responsive & Modern UI</li>
            </ul>
          </div>

          <div className="bg-slate-900 p-6 rounded-xl border border-slate-800">
            <h2 className="text-2xl font-semibold text-cyan-400 mb-4">
              💻 Tech Stack
            </h2>

            <ul className="space-y-3 text-gray-300">
              <li>Frontend: React + TypeScript</li>
              <li>Styling: Tailwind CSS + shadcn/ui</li>
              <li>State Management: Zustand</li>
              <li>Backend: Spring Boot</li>
              <li>Security: Spring Security + JWT</li>
              <li>Database: MySQL</li>
              <li>API Communication: Axios</li>
            </ul>
          </div>
        </div>

        <div className="mt-10 bg-gradient-to-r from-cyan-500/10 to-blue-500/10 border border-cyan-500/20 rounded-xl p-8">
          <h2 className="text-3xl font-bold text-cyan-400 mb-4">
            Our Mission
          </h2>

          <p className="text-gray-300 leading-8">
            Our goal is to provide a secure, reliable, and scalable authentication
            system that follows modern web security standards. This project serves
            as a practical implementation of enterprise-level authentication using
            React, Spring Boot, JWT, Refresh Tokens, and secure cookie-based
            session management.
          </p>
        </div>
      </div>
    </div>
  );
}

export default About;