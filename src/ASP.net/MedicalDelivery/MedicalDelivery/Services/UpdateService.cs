using MedicalDelivery.Helpers;
using MedicalDelivery.Models;
using Microsoft.AspNetCore.Mvc;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.EntityFrameworkCore;

namespace MedicalDelivery.Services
{
    public interface IUpdateService
    {
        Update create(Update update, int deliveryID);
        IEnumerable<Update> getAll();
        IEnumerable<Update> getByDeliveryID(int deliveryID);
        Update getByID(int ID);
    }
    public class UpdateService : IUpdateService
    {
        private LibraryContext context;
        public UpdateService(LibraryContext context)
        {
            this.context = context;
        }

        public Update create(Update update, int deliveryID)
        {
            var delivery = context.deliveries.Include(x => x.medicine).FirstOrDefault(x => x.ID == deliveryID);
            if (delivery.status == 3 || delivery.status == 0 || delivery.status == 4)
            {
                throw new AppException("Delivery is not acceping updates");
            }
            update.delivery = delivery;
            var medicine = delivery.medicine;
            Trace.WriteLine(delivery.ID.ToString());
            
            Trace.WriteLine(delivery.medicine.name);
            if (update.temp >= medicine.maxTemp || update.temp <= medicine.minTemp || update.humid >= medicine.maxHumid || update.humid <= medicine.minHumid || update.orientation || update.movement) {
                delivery.status = 2;
                delivery.notification = true;
                delivery.doctorNotification = true;
                delivery.riderNotification = true;
            }
            update.timeStamp = DateTime.Now;
            context.updates.Add(update);
            context.SaveChanges();
            return update;
        }

        public IEnumerable<Update> getAll()
        {
            return context.updates;
        }

        public IEnumerable<Update> getByDeliveryID(int deliveryID)
        {
            return context.updates.Where(x => x.delivery.ID == deliveryID);
        }

        public Update getByID(int ID)
        {
            var update = context.updates.Find(ID);
            return update;
        }
    }
}
