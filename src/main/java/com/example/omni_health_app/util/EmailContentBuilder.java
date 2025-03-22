package com.example.omni_health_app.util;

import com.example.omni_health_app.domain.entity.UserAppointmentSchedule;

public final class EmailContentBuilder {


    public static String buildUserEmailContentForAppointmentCreation(UserAppointmentSchedule appointment) {
        return String.format(
                """
                <html>
                <head>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            line-height: 1.6;
                            color: #333333;
                        }
                        .header {
                            background-color: #4CAF50;
                            color: white;
                            padding: 10px 20px;
                            text-align: center;
                            font-size: 24px;
                        }
                        .content {
                            padding: 20px;
                        }
                        .footer {
                            margin-top: 20px;
                            text-align: center;
                            font-size: 14px;
                            color: #777777;
                        }
                        .details {
                            margin: 10px 0;
                            padding: 10px;
                            background-color: #f9f9f9;
                            border: 1px solid #dddddd;
                            border-radius: 5px;
                        }
                    </style>
                </head>
                <body>
                    <div class="header">Appointment Created</div>
                    <div class="content">
                        <p>Dear <strong>%s</strong>,</p>
                        <p>Congrats! Your appointment is successfully created with <strong>Dr. %s</strong>.</p>
                        <div class="details">
                            <p><strong>Date:</strong> %s</p>
                            <p><strong>Time:</strong> %s</p>
                            <p><strong>Location:</strong> %s</p>
                        </div>
                        <p>We look forward to seeing you fit soon!</p>
                    </div>
                    <div class="footer">Best regards,<br>Omni Health App Team</div>
                </body>
                </html>
                """,
                appointment.getUserDetail().getFirstName() + " " + appointment.getUserDetail().getLastName(),
                appointment.getDoctorDetail().getFirstName() + " " + appointment.getDoctorDetail().getLastName(),
                appointment.getAppointmentDateTime().toLocalDate(),
                appointment.getAppointmentDateTime().toLocalTime(),
                appointment.getDoctorDetail().getLocation()
        );
    }

    public static String buildUserEmailContentForAppointmentUpdate(UserAppointmentSchedule appointment) {
        return String.format(
                """
                <html>
                <head>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            line-height: 1.6;
                            color: #333333;
                        }
                        .header {
                            background-color: #4CAF50;
                            color: white;
                            padding: 10px 20px;
                            text-align: center;
                            font-size: 24px;
                        }
                        .content {
                            padding: 20px;
                        }
                        .footer {
                            margin-top: 20px;
                            text-align: center;
                            font-size: 14px;
                            color: #777777;
                        }
                        .details {
                            margin: 10px 0;
                            padding: 10px;
                            background-color: #f9f9f9;
                            border: 1px solid #dddddd;
                            border-radius: 5px;
                        }
                    </style>
                </head>
                <body>
                    <div class="header">Appointment Updated</div>
                    <div class="content">
                        <p>Dear <strong>%s</strong>,</p>
                        <p>Congrats! Your appointment is successfully updated with <strong>Dr. %s</strong>.New details are mentioned below</p>
                        <div class="details">
                            <p><strong>Date:</strong> %s</p>
                            <p><strong>Time:</strong> %s</p>
                            <p><strong>Location:</strong> %s</p>
                        </div>
                        <p>We look forward to seeing you fit soon!</p>
                    </div>
                    <div class="footer">Best regards,<br>Omni Health App Team</div>
                </body>
                </html>
                """,
                appointment.getUserDetail().getFirstName() + " " + appointment.getUserDetail().getLastName(),
                appointment.getDoctorDetail().getFirstName() + " " + appointment.getDoctorDetail().getLastName(),
                appointment.getAppointmentDateTime().toLocalDate(),
                appointment.getAppointmentDateTime().toLocalTime(),
                appointment.getDoctorDetail().getLocation()
        );
    }

    public static String buildUserEmailContentForAppointmentCancellation(UserAppointmentSchedule appointment) {
        return String.format(
                """
                <html>
                <head>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            line-height: 1.6;
                            color: #333333;
                        }
                        .header {
                            background-color: #4CAF50;
                            color: white;
                            padding: 10px 20px;
                            text-align: center;
                            font-size: 24px;
                        }
                        .content {
                            padding: 20px;
                        }
                        .footer {
                            margin-top: 20px;
                            text-align: center;
                            font-size: 14px;
                            color: #777777;
                        }
                        .details {
                            margin: 10px 0;
                            padding: 10px;
                            background-color: #f9f9f9;
                            border: 1px solid #dddddd;
                            border-radius: 5px;
                        }
                    </style>
                </head>
                <body>
                    <div class="header">Appointment Cancelled</div>
                    <div class="content">
                        <p>Dear <strong>%s</strong>,</p>
                        <p>Sorry to let you go! Your appointment is successfully cancelled with <strong>Dr. %s</strong>.</p>
                        <div class="details">
                            <p><strong>Date:</strong> %s</p>
                            <p><strong>Time:</strong> %s</p>
                            <p><strong>Location:</strong> %s</p>
                        </div>
                        <p>How ever we look forward to see you again for any medical help!</p>
                    </div>
                    <div class="footer">Best regards,<br>Omni Health App Team</div>
                </body>
                </html>
                """,
                appointment.getUserDetail().getFirstName() + " " + appointment.getUserDetail().getLastName(),
                appointment.getDoctorDetail().getFirstName() + " " + appointment.getDoctorDetail().getLastName(),
                appointment.getAppointmentDateTime().toLocalDate(),
                appointment.getAppointmentDateTime().toLocalTime(),
                appointment.getDoctorDetail().getLocation()
        );
    }

    public static String buildDoctorEmailContentForAppointmentCreation(UserAppointmentSchedule appointment) {
        return String.format(
                """
                <html>
                <head>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            line-height: 1.6;
                            color: #333333;
                        }
                        .header {
                            background-color: #4CAF50;
                            color: white;
                            padding: 10px 20px;
                            text-align: center;
                            font-size: 24px;
                        }
                        .content {
                            padding: 20px;
                        }
                        .footer {
                            margin-top: 20px;
                            text-align: center;
                            font-size: 14px;
                            color: #777777;
                        }
                        .details {
                            margin: 10px 0;
                            padding: 10px;
                            background-color: #f9f9f9;
                            border: 1px solid #dddddd;
                            border-radius: 5px;
                        }
                    </style>
                </head>
                <body>
                    <div class="header">Appointment Requested</div>
                    <div class="content">
                        <p>Dear <strong>Dr. %s</strong>,</p>
                        <p>An appointment is successfully scheduled for <strong>Patient %s</strong>.</p>
                        <div class="details">
                            <p><strong>Date:</strong> %s</p>
                            <p><strong>Time:</strong> %s</p>
                            <p><strong>Location:</strong> %s</p>
                        </div>
                        <p>Please check if action required from your side based on user notes</p>
                    </div>
                    <div class="footer">Best regards,<br>Omni Health App Team</div>
                </body>
                </html>
                """,
                appointment.getDoctorDetail().getFirstName() + " " + appointment.getDoctorDetail().getLastName(),
                appointment.getUserDetail().getFirstName() + " " + appointment.getUserDetail().getLastName(),
                appointment.getAppointmentDateTime().toLocalDate(),
                appointment.getAppointmentDateTime().toLocalTime(),
                appointment.getDoctorDetail().getLocation()
        );
    }

    public static String buildDoctorEmailContentForAppointmentCancellation(UserAppointmentSchedule appointment) {
        return String.format(
                """
                <html>
                <head>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            line-height: 1.6;
                            color: #333333;
                        }
                        .header {
                            background-color: #4CAF50;
                            color: white;
                            padding: 10px 20px;
                            text-align: center;
                            font-size: 24px;
                        }
                        .content {
                            padding: 20px;
                        }
                        .footer {
                            margin-top: 20px;
                            text-align: center;
                            font-size: 14px;
                            color: #777777;
                        }
                        .details {
                            margin: 10px 0;
                            padding: 10px;
                            background-color: #f9f9f9;
                            border: 1px solid #dddddd;
                            border-radius: 5px;
                        }
                    </style>
                </head>
                <body>
                    <div class="header">Appointment Requested</div>
                    <div class="content">
                        <p>Dear <strong>Dr. %s</strong>,</p>
                        <p>The appointment is successfully cancelled for <strong>Patient %s</strong>.</p>
                        <div class="details">
                            <p><strong>Date:</strong> %s</p>
                            <p><strong>Time:</strong> %s</p>
                            <p><strong>Location:</strong> %s</p>
                        </div>
                        <p>As of now no action is required from your side</p>
                    </div>
                    <div class="footer">Best regards,<br>Omni Health App Team</div>
                </body>
                </html>
                """,
                appointment.getDoctorDetail().getFirstName() + " " + appointment.getDoctorDetail().getLastName(),
                appointment.getUserDetail().getFirstName() + " " + appointment.getUserDetail().getLastName(),
                appointment.getAppointmentDateTime().toLocalDate(),
                appointment.getAppointmentDateTime().toLocalTime(),
                appointment.getDoctorDetail().getLocation()
        );
    }

    public static String buildDoctorEmailContentForAppointmentUpdate(UserAppointmentSchedule appointment) {
        return String.format(
                """
                <html>
                <head>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            line-height: 1.6;
                            color: #333333;
                        }
                        .header {
                            background-color: #4CAF50;
                            color: white;
                            padding: 10px 20px;
                            text-align: center;
                            font-size: 24px;
                        }
                        .content {
                            padding: 20px;
                        }
                        .footer {
                            margin-top: 20px;
                            text-align: center;
                            font-size: 14px;
                            color: #777777;
                        }
                        .details {
                            margin: 10px 0;
                            padding: 10px;
                            background-color: #f9f9f9;
                            border: 1px solid #dddddd;
                            border-radius: 5px;
                        }
                    </style>
                </head>
                <body>
                    <div class="header">Appointment Requested</div>
                    <div class="content">
                        <p>Dear <strong>Dr. %s</strong>,</p>
                        <p>A previous appointment is successfully updated for <strong>Patient %s</strong>. New details are as follows, check your dashboard for updated details</p>
                        <div class="details">
                            <p><strong>Date:</strong> %s</p>
                            <p><strong>Time:</strong> %s</p>
                            <p><strong>Location:</strong> %s</p>
                        </div>
                        <p>Please check if action required from your side based on user notes</p>
                    </div>
                    <div class="footer">Best regards,<br>Omni Health App Team</div>
                </body>
                </html>
                """,
                appointment.getDoctorDetail().getFirstName() + " " + appointment.getDoctorDetail().getLastName(),
                appointment.getUserDetail().getFirstName() + " " + appointment.getUserDetail().getLastName(),
                appointment.getAppointmentDateTime().toLocalDate(),
                appointment.getAppointmentDateTime().toLocalTime(),
                appointment.getDoctorDetail().getLocation()
        );
    }
}
