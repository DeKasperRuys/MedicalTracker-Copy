using MedicalDelivery.Helpers;
using MedicalDelivery.Models;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace MedicalDelivery.Services
{
    public interface IDeliveryService
    {
        Delivery create(Delivery delivery, int hospitalID, int medicineID);
        IEnumerable<Delivery> getAll();
        Delivery getByID(int ID);
        IEnumerable<Delivery> getByHospitalID(int id);
        IEnumerable<Delivery> getByRiderID(int id);
        IEnumerable<Delivery> getNewDeliveries();
        Delivery readNotificationDoctor(int ID);
        Delivery readNotificationRider(int ID);
        Delivery response(int ID);
        Delivery updateDelivery (Delivery delivery, int ID);

    }
    public class DeliveryService : IDeliveryService
    {
        private LibraryContext context;
        public DeliveryService(LibraryContext context)
        {
            this.context = context;
        }

        public Delivery create(Delivery delivery, int hospitalID, int medicineID)
        {
            if (!context.hospitals.Any(x => x.ID == hospitalID))
                throw new AppException("Hospital not Found");
            if (!context.medicines.Any(x => x.ID == medicineID))
                throw new AppException("Medicine not Found");
            if (delivery.amount == 0)
                throw new AppException("Amount can't be zero");
            var hospital = context.hospitals.Find(hospitalID);
            var medicine = context.medicines.Find(medicineID);
            delivery.medicine = medicine;
            delivery.hospital = hospital;
            context.Add(delivery);
            context.SaveChanges();
            return delivery;
        }

        public Delivery updateDelivery(Delivery delivery, int ID)
        {
            var oldDelivery = context.deliveries.Find(ID);
            oldDelivery.status = delivery.status;
            if (delivery.response) {
                oldDelivery.response = true;
            }
            if (delivery.rider != null) {
                  Rider rider = context.riders.Find(delivery.rider.ID);
                  oldDelivery.rider = rider;
            }
            context.SaveChanges();
            return oldDelivery;
        }

        public IEnumerable<Delivery> getAll()
        {
            return context.deliveries.Include(x => x.medicine).Include(x => x.hospital);
        }

        public IEnumerable<Delivery> getByHospitalID(int id)
        {
            return context.deliveries.Where(x => x.hospital.ID == id).Include(x => x.medicine);
        }
        public IEnumerable<Delivery> getByRiderID(int id)
        {
            return context.deliveries.Where(x => x.rider.ID == id).Include(x => x.medicine).Include(x => x.hospital);
        }
        public IEnumerable<Delivery> getNewDeliveries()
        {
            return context.deliveries.Where(x => x.status == 0).Include(x => x.medicine).Include(x => x.hospital).Include(x => x.rider);
        }
        public Delivery getByID(int ID)
        {
            var delivery = context.deliveries.Include(x => x.hospital).Include(x => x.rider).Include(x => x.medicine).SingleOrDefault(x => x.ID == ID);
            return delivery;
        }

        public Delivery readNotificationDoctor(int ID)
        {
            var delivery = context.deliveries.Find(ID);
            delivery.doctorNotification = false;
            context.SaveChanges();
            return delivery;
        }
        public Delivery readNotificationRider(int ID)
        {
            var delivery = context.deliveries.Find(ID);
            delivery.riderNotification = false;
            context.SaveChanges();
            return delivery;
        }
        public Delivery response(int ID) {
            var delivery = context.deliveries.Find(ID);
            if (delivery.response)
            {
                delivery.response = false;
                delivery.notification = false;
            }
            else {
                delivery.response = true;
            }
            context.SaveChanges();
            return delivery;
        }
    }
}
