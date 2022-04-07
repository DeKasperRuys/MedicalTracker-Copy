using MedicalDelivery.Helpers;
using MedicalDelivery.Models;
using MedicalDelivery.Models.Payload;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Threading.Tasks;

namespace MedicalDelivery.Services
{
    public interface IPayloadService
    {
        Update create(Payload payload, int ID);
    }
    public class PayloadService : IPayloadService
    {
        private LibraryContext context;
        public PayloadService(LibraryContext context)
        {
            this.context = context;
        }
        public Update create(Payload payload, int ID)
        {
            Update update = new Update();
            var delivery = context.deliveries.Include(x => x.medicine).FirstOrDefault(x => x.ID == ID);
            if (delivery.status == 3 || delivery.status == 0 || delivery.status == 4) {
                throw new AppException("Delivery is not acceping updates");
            }
            update.delivery = delivery;
            update.humid = payload.payload_fields.humid;
            update.movement = payload.payload_fields.movement;
            update.orientation = payload.payload_fields.orientation;
            update.temp = payload.payload_fields.temp;
            update.lat = payload.payload_fields.lat;
            update.lon = payload.payload_fields.lon;
            update.timeStamp = DateTime.Now;
            var medicine = delivery.medicine;
            if (update.temp >= medicine.maxTemp || update.temp <= medicine.minTemp || update.humid >= medicine.maxHumid || update.humid <= medicine.minHumid || update.orientation || update.movement)
            {
                delivery.status = 2;
                delivery.notification = true;
                delivery.doctorNotification = true;
                delivery.riderNotification = true;
            }
            context.updates.Add(update);
            context.SaveChanges();
            return update;
        }
    }
}